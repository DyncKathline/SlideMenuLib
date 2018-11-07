package com.qmai.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.qmai.slidemenulib.SlidingLayout;

import java.util.ArrayList;
import java.util.List;

public class SlideBackActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private SlidingLayout mSlidingLayout;
    private RecyclerView mRecyclerView;
    private SlideAdapter mAdapter;
    private List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_back);
        mSlidingLayout = findViewById(R.id.slidingLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mSlidingLayout.setSlidingListener(new SlidingLayout.SlidingListener() {
            @Override
            public void slidingInterceptLeft() {
                Log.e(TAG, "slidingInterceptRight: 左滑");
            }

            @Override
            public void slidingInterceptRight() {
                Log.e(TAG, "slidingInterceptRight: 右滑");
                finish();
            }
        });

        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager( this));
        mRecyclerView.setAdapter(mAdapter = new SlideAdapter(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setData(data);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            data.add("测试数据" + i);
        }
    }
}
