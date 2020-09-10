package com.qmai.crashlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.qmai.crashlib.tool.ToolFileUtils;
import com.qmai.crashlib.tool.ToolLogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *     @author kathline
 *     time  : 2020/7/10
 *     desc  : 异常处理保存文件类
 *     revise:
 * </pre>
 */
public final class CrashFileUtils {


    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";
    /**
     * 额外信息写入
     */
    private static String headContent;
    /**
     * 时间转换
     */
    private static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static String crashTime;
    private static String crashHead;
    private static String versionName;
    private static String versionCode;

    public static void setHeadContent(String headContent) {
        CrashFileUtils.headContent = headContent;
    }

    public static String saveCrashInfo(Context context, Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        //判断有没有额外信息需要写入
        if (!TextUtils.isEmpty(headContent)) {
            pw.println(headContent);
        }
        //print(ex);
        //写入设备信息
        pw.println(crashHead);
        ex.printStackTrace(pw);
        pw.flush();
        //异常信息
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        return sw.toString();
    }

    /**
     * 保存错误信息到文件中
     * 一个崩溃保存到一个txt文本文件中
     * 后期需求：1.过了7天自动清除日志；2.针对文件的大小限制；3.文件写入
     * @param context
     * @param ex
     */
    public static void saveCrashInfoInFile(Context context, CrashHandler.Builder builder, Throwable ex){
        deleteFile(context, builder);
        initCrashHead(context);
        dumpExceptionToFile(context, ex);
        //saveCrashInfoToFile(context,ex);
    }

    private static void initCrashHead(Context context) {
        //崩溃时间
        crashTime = dataFormat.format(new Date(System.currentTimeMillis()));
        //版本信息
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = String.valueOf(pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //组合Android相关信息
        /*crashHead =
                "\n崩溃的时间\b\b\b:\b\b" + crashTime +
                        "\n系统硬件商\b\b\b:\b\b" + Build.MANUFACTURER +
                        "\n设备的品牌\b\b\b:\b\b" + Build.BRAND +
                        "\n手机的型号\b\b\b:\b\b" + Build.MODEL +
                        "\n设备版本号\b\b\b:\b\b" + Build.ID +
                        "\nCPU的类型\b\b\b:\b\b" + Build.CPU_ABI +
                        "\n系统的版本\b\b\b:\b\b" + Build.VERSION.RELEASE +
                        "\n系统版本值\b\b\b:\b\b" + Build.VERSION.SDK_INT +
                        "\n当前的版本\b\b\b:\b\b" + versionName + "—" + versionCode +
                        "\n\n";*/
        StringBuilder sb = new StringBuilder();
        sb.append("\n软件APPLICATION_ID:").append(BuildConfig.APPLICATION_ID);
        sb.append("\n是否是DEBUG版本:").append(BuildConfig.BUILD_TYPE);
        sb.append("\n崩溃的时间:").append(crashTime);
        sb.append("\n系统硬件商:").append(Build.MANUFACTURER);
        sb.append("\n设备的品牌:").append(Build.BRAND);
        sb.append("\n手机的型号:").append(Build.MODEL);
        sb.append("\n设备版本号:").append(Build.ID);
        sb.append("\nCPU的类型:").append(Build.CPU_ABI);
        sb.append("\n系统的版本:").append(Build.VERSION.RELEASE);
        sb.append("\n系统版本值:").append(Build.VERSION.SDK_INT);
        sb.append("\n当前的版本:").append(versionName).append("—").append(versionCode);
        sb.append("\n\n");
        crashHead = sb.toString();
    }

    private static void dumpExceptionToFile(Context context , Throwable ex) {
        File file = null;
        PrintWriter pw = null;
        try {
            //Log保存路径
            // SDCard/Android/data/<application package>/cache
            // data/data/<application package>/cache
            File dir = new File(ToolFileUtils.getCrashLogPath(context));
            if (!dir.exists()) {
                boolean ok = dir.mkdirs();
                if (!ok) {
                    return;
                }
            }
            //Log文件的名字
            String fileName = "V" + versionName + "_" + crashTime + CRASH_REPORTER_EXTENSION;
            file = new File(dir, fileName);
            if (!file.exists()) {
                boolean createNewFileOk = file.createNewFile();
                if (!createNewFileOk) {
                    return;
                }
            }
//            ToolLogUtils.i(CrashHandler.TAG, "保存异常的log文件名称：" + fileName);
//            ToolLogUtils.i(CrashHandler.TAG, "保存异常的log文件file：" + file);
            //开始写日志
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //判断有没有额外信息需要写入
            if (!TextUtils.isEmpty(headContent)) {
                pw.println(headContent);
            }
            //print(ex);
            //写入设备信息
            pw.println(crashHead);
            //导出异常的调用栈信息
            ex.printStackTrace(pw);
            //异常信息
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            //重新命名文件
            String string = ex.toString();
            String splitEx;
            if (string.contains(":")){
                splitEx = ex.toString().split(":")[0];
            } else {
                splitEx = "java.lang.Exception";
            }
            String newName = "V" + versionName + "_" + crashTime + "_" + splitEx + CRASH_REPORTER_EXTENSION;
            File newFile = new File(dir, newName);
            //重命名文件
            ToolFileUtils.renameFile(file.getPath(), newFile.getPath());
            //路径：/storage/emulated/0/Android/data/包名/cache/crashLogs
            //file       V1.0_2020-09-02_09:05:01.txt
            //newFile    V1.0_2020-09-02_09:05:01_java.lang.NullPointerException.txt
            ToolLogUtils.i(CrashHandler.TAG, "保存异常的log文件路径：" + file.getPath() + "----新路径---"+newFile.getPath());
        } catch (Exception e) {
            ToolLogUtils.e(CrashHandler.TAG, "保存日志失败：" + e.toString());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public static void deleteFile(Context context, CrashHandler.Builder builder) {
        String path = ToolFileUtils.getCrashLogPath(context);
        List<String> allName = getFilesAllName(path);
        if(builder.openExpiryTime) {
            long expiryTime = builder.expiryTime;
            long now = new Date().getTime();
            for (int i = 0; i < allName.size(); i++) {
                String name = allName.get(i);
                String[] split = name.split("/");
                long time = getStringToDate(getDate(split[split.length - 1]), "yyyy-MM-dd HH:mm:ss");
//            Log.d(TAG, "onClick:--- " + split[split.length - 1]);
//            Log.d(TAG, "onClick:--- " + now + ",time: " + time + ",expiryTime: " + (now - time));
                if ((now - time) / 1000 >= expiryTime) {
                    File file = new File(name);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }else if(builder.openMaxLogCount) {
            int count;
            if(builder.maxLogCount > allName.size()) {
                count = 0;
            }else {
                count = allName.size() - builder.maxLogCount;
            }
            for (int i = 0; i < count; i++) {
                String name = allName.get(i);
                File file = new File(name);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
//        Log.d(TAG, "onClick: " + allName.toString());
    }

    public static List<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> s = new ArrayList<>();
        if (files == null) {
//            Log.e("error", "空目录");
            return s;
        }
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {//旧文件排在前面
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        for (int i = 0; i < files.length; i++) {
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }

    public static String getDate(String strDate) {
        String s = "";
        Pattern p = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
        Matcher m = p.matcher(strDate);
        while (m.find()) {
            s += m.group();
        }
        return s;
    }

    /**
     * 将字符串转为时间戳
     * * @param dateString
     * * @param pattern
     * * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static void print(Throwable thr) {
        StackTraceElement[] stackTraces = thr.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            String clazzName = stackTrace.getClassName();
            String fileName = stackTrace.getFileName();
            int lineNumber = stackTrace.getLineNumber();
            String methodName = stackTrace.getMethodName();
            ToolLogUtils.i("printThrowable------"+clazzName+"----"
                    +fileName+"------"+lineNumber+"----"+methodName);
        }
    }

    private static StackTraceElement parseThrowable(Throwable ex , Context context) {
        if (ex == null || ex.getStackTrace() == null || ex.getStackTrace().length == 0) {
            return null;
        }
        if (context == null){
            return null;
        }
        StackTraceElement[] stackTrace = ex.getStackTrace();
        StackTraceElement element;
        String packageName = context.getPackageName();
        for (StackTraceElement ele : stackTrace) {
            if (ele.getClassName().contains(packageName)) {
                element = ele;
                String clazzName = element.getClassName();
                String fileName = element.getFileName();
                int lineNumber = element.getLineNumber();
                String methodName = element.getMethodName();
                ToolLogUtils.i("printThrowable----1--"+clazzName+"----"
                        +fileName+"------"+lineNumber+"----"+methodName);
                return element;
            }
        }
        element = stackTrace[0];
        String clazzName = element.getClassName();
        String fileName = element.getFileName();
        int lineNumber = element.getLineNumber();
        String methodName = element.getMethodName();
        ToolLogUtils.i("printThrowable----2--"+clazzName+"----"
                +fileName+"------"+lineNumber+"----"+methodName);
        return element;
    }

    /**
     * 获取错误报告文件路径
     *
     * @param ctx
     * @return
     */
    @Deprecated
    public static String[] getCrashReportFiles(Context ctx) {
        File filesDir = new File(getCrashFilePath(ctx));
        String[] fileNames = filesDir.list();
        int length = fileNames.length;
        String[] filePaths = new String[length];
        for (int i = 0; i < length; i++) {
            filePaths[i] = getCrashFilePath(ctx) + fileNames[i];
        }
        return filePaths;
    }


    /**
     * 保存错误信息到文件中
     * @param ex
     * @return
     */
    @Deprecated
    public static void saveCrashInfoToFile(Context context , Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        StringBuilder sb = new StringBuilder();
        @SuppressLint("SimpleDateFormat")
        String now = dataFormat.format(new Date());
        sb.append("TIME:").append(now);//崩溃时间
        //程序信息
        sb.append("\nAPPLICATION_ID:").append(BuildConfig.APPLICATION_ID);//软件APPLICATION_ID
        sb.append("\nVERSION_CODE:").append(BuildConfig.VERSION_CODE);//软件版本号
        sb.append("\nVERSION_NAME:").append(BuildConfig.VERSION_NAME);//VERSION_NAME
        sb.append("\nBUILD_TYPE:").append(BuildConfig.BUILD_TYPE);//是否是DEBUG版本
        //设备信息
        sb.append("\nMODEL:").append(Build.MODEL);
        sb.append("\nRELEASE:").append(Build.VERSION.RELEASE);
        sb.append("\nSDK:").append(Build.VERSION.SDK_INT);
        sb.append("\nEXCEPTION:").append(ex.getLocalizedMessage());
        sb.append("\nSTACK_TRACE:").append(result);
        String crashFilePath = getCrashFilePath(context);
        if (crashFilePath!=null && crashFilePath.length()>0){
            try {
                ToolLogUtils.w(CrashHandler.TAG, "handleException---输出路径-----"+crashFilePath);
                FileWriter writer = new FileWriter( crashFilePath+ now + CRASH_REPORTER_EXTENSION);
                writer.write(sb.toString());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    @Deprecated
    private static String getCrashFilePath(Context context) {
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath()
                    + "/" + context.getResources().getString(R.string.app_name) + "/Crash/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

}
