package com.qmai.crashlib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.qmai.crashlib.tool.ToolLogUtils;

/**
 * <pre>
 *     @author kathline
 *     time  : 2020/7/10
 *     desc  : 异常处理类
 *     revise:
 * </pre>
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;

    private Builder mBuilder;

    public static class Builder {

        /**
         * 没有开启只会执行CrashListener，不会保存错误日志
         */
        public boolean mEnable = true;
        /**
         * 是否立即打开crash详情页，当crash时
         */
        public boolean mOpenNowCrash = true;
        /**
         * 监听
         */
        public CrashListener listener;
        /**
         * 文件过期时间
         */
        public long expiryTime = 60 * 60 * 24 * 7;
        public boolean openExpiryTime;
        /**
         * 文件最大数量
         */
        public int maxLogCount = 30;
        public boolean openMaxLogCount;

        public Builder setDebugEnable(boolean enable) {
            this.mEnable = enable;
            return this;
        }

        public void setOpenNowCrash(boolean mOpenNowCrash) {
            this.mOpenNowCrash = mOpenNowCrash;
        }

        public Builder setExpiryTime(long expiryTime, boolean enable) {
            this.expiryTime = expiryTime;
            this.openExpiryTime = enable;
            return this;
        }

        public Builder setMaxLogCount(int maxLogCount, boolean enable) {
            this.maxLogCount = maxLogCount;
            this.openMaxLogCount = enable;
            return this;
        }

        public Builder setOnCrashListener(CrashListener listener) {
            this.listener = listener;
            return this;
        }

    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器
     *
     * @param context
     * @param builder
     */
    public void init(Application context, Builder builder) {
        LifecycleCallback.getInstance().init(context);
        mContext = context;
        //获取系统默认的UncaughtExceptionHandler
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        //设置一个处理者当一个线程突然因为一个未捕获的异常而终止时将自动被调用。
        //未捕获的异常处理的控制第一个被当前线程处理，如果该线程没有捕获并处理该异常，其将被线程的ThreadGroup对象处理，最后被默认的未捕获异常处理器处理。
        Thread.setDefaultUncaughtExceptionHandler(this);
        mBuilder = builder;
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     * 该方法来实现对运行时线程进行异常处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(mBuilder ==null) return;
        initCustomBug(ex);
        boolean isHandle = handleException(ex);
        if (mDefaultHandler != null && !isHandle) {
            //收集完信息后，交给系统自己处理崩溃
            //uncaughtException (Thread t, Throwable e) 是一个抽象方法
            //当给定的线程因为发生了未捕获的异常而导致终止时将通过该方法将线程对象和异常对象传递进来。
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //否则自己处理
            if (mContext instanceof Application) {
                ToolLogUtils.w(TAG, "handleException--- ex----重启activity-");
                if (mBuilder.listener != null) {
                    mBuilder.listener.againStartApp();
                }
            }
        }
        CrashToolUtils.killCurrentProcess(true);
    }

    /**
     * 初始化百度
     * @param ex
     */
    private void initCustomBug(Throwable ex) {
        //自定义上传crash，支持开发者上传自己捕获的crash数据
        //StatService.recordException(mContext, ex);
        if (mBuilder.listener != null){
            //捕获监听中异常，防止外部开发者使用方代码抛出异常时导致的反复调用
            try {
                mBuilder.listener.recordException(ex);
            } catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * 开发者可以根据自己的情况来自定义异常处理逻辑
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            ToolLogUtils.w(TAG, "handleException--- ex==null");
            return false;
        }
        //收集crash信息
        String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
//        ToolLogUtils.w(TAG, "handleException--- ex-----"+msg);
        ex.printStackTrace();
        if(mBuilder.mEnable) {
            //收集设备信息
            //保存错误报告文件
            CrashFileUtils.saveCrashInfoInFile(mContext, mBuilder, ex);
            if(mBuilder.mOpenNowCrash) {
                String crashInfo = CrashFileUtils.saveCrashInfo(mContext, ex);
                Intent intent = new Intent(mContext, CrashDetailsActivity.class);
                intent.putExtra(CrashDetailsActivity.IntentKey_Content, crashInfo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
        return true;
    }


}
