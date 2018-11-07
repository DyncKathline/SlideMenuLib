package com.qmai.slidemenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.slideMenu:
                startActivity(new Intent(MainActivity.this, SlideMenuActivity.class));
                break;
            case R.id.slideBack:
                startActivity(new Intent(MainActivity.this, SlideBackActivity.class));
                break;
        }
    }

}
