package com.qmai.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qmai.slidemenulib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

public class SlideMenuActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private SlidingMenu mSlidingMenu;
    private RecyclerView mRecyclerView;
    private SlideAdapter mAdapter;
    private List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu);

        mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        initData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager( this));
        mRecyclerView.setAdapter(mAdapter = new SlideAdapter(this, mRecyclerView));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setData(data);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            data.add("测试数据" + i);
        }
    }
}
