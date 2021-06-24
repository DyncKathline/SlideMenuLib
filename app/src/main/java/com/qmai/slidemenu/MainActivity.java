package com.qmai.slidemenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.qmai.crashlib.CrashHandler;
import com.qmai.crashlib.CrashListener;
import com.qmai.dialoglib.CustomDialog;
import com.qmai.dialoglib.CustomDialogFragment;
import com.qmai.dialoglib.CustomPopupWindow;

public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    Button btnPopupWindow;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPopupWindow = findViewById(R.id.popupWindow);
        CrashHandler.Builder builder = new CrashHandler.Builder();
        builder.setMaxLogCount(10, true)
                .setOnCrashListener(new CrashListener() {
                    /**
                     * 重启app
                     */
                    @Override
                    public void againStartApp() {
                        System.out.println("崩溃重启----------againStartApp------");
                    }

                    /**
                     * 自定义上传crash，支持开发者上传自己捕获的crash数据
                     * @param ex                        ex
                     */
                    @Override
                    public void recordException(Throwable ex) {
                        System.out.println("崩溃重启----------recordException------");
                    }
                });
        CrashHandler.getInstance().init(getApplication(), builder);
        rl = findViewById(R.id.slidingMenu);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.itemDecoration:
                startActivity(new Intent(MainActivity.this, ItemDecorationActivity.class));
                break;
            case R.id.gallery:
                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
                break;
            case R.id.dialog:
                showDialog();
                break;
            case R.id.dialogFragment:
                showDialogFragment();
                break;
            case R.id.popupWindow:
                showPopupWindow();
                break;
        }
    }

    private void showPopupWindow() {
        CustomPopupWindow.Builder builder = new CustomPopupWindow.Builder(this);
        builder
                .setContentView(R.layout.dialog)
//                .setFullScreen(true)
                .setBackgroundDrawable(true)
                .setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setViewListener(new CustomPopupWindow.Builder.OnInitListener() {
                    @Override
                    public void init(CustomPopupWindow popupWindow, View view) {
                        Button text1 = view.findViewById(R.id.button);
                        text1.setText("你好");
                        popupWindow.showAsDropDown(btnPopupWindow);
                    }
                })
                .build();
    }

    private void showDialogFragment() {
        CustomDialogFragment.Builder builder = new CustomDialogFragment.Builder(this);
        builder
                .setContentView(R.layout.dialog)
//                .setFullScreen(true)
                .setExistDialogLined(true)
                .setBackgroundDrawable(true)
                .setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnInitListener(new CustomDialogFragment.Builder.OnInitListener() {
                    @Override
                    public void init(CustomDialogFragment dialogFragment, View view) {
                        Button text1 = view.findViewById(R.id.button);
                        text1.setText("你好");
                    }
                })
                .build(getFragmentManager(), "xxxDialogFragment");
    }

    private void showDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder
                .setContentView(R.layout.dialog)
//                .setFullScreen(true)
                .setExistDialogLined(true)
                .setBackgroundDrawable(true)
                .setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnInitListener(new CustomDialog.Builder.OnInitListener() {
                    @Override
                    public void init(CustomDialog customDialog) {
                        Button text1 = customDialog.findViewById(R.id.button);
                        text1.setText("你好");
                    }
                })
                .build();
    }

}
