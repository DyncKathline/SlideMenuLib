package com.qmai.crashlib;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.qmai.crashlib.tool.ToolAppManager;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time  : 2015/03/22
 *     desc  : 生命周期管理类
 *     revise:
 * </pre>
 */
public class LifecycleCallback implements Application.ActivityLifecycleCallbacks{

    public static LifecycleCallback getInstance() {
        return HolderClass.INSTANCE;
    }


    private final static class HolderClass {
        private final static LifecycleCallback INSTANCE = new LifecycleCallback();
    }


    private LifecycleCallback() {}

    /**
     * 必须在 Application 的 onCreate 方法中调用
     */
    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ToolAppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //将当前Activity移除到容器
        ToolAppManager.getAppManager().removeActivity(activity);
    }

}
