package com.qmai.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        GalleryLayoutManager manager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
        manager.attach(recyclerView);
        //设置滑动缩放效果
        manager.setItemTransformer(new GalleryLayoutManager.ItemTransformer() {
            @Override
            public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
                //以圆心进行缩放
                item.setPivotX(item.getWidth() / 2.0f);
                item.setPivotY(item.getHeight() / 2.0f);
                float scale = 1 - 0.3f * Math.abs(fraction);
                item.setScaleX(scale);
                item.setScaleY(scale);
            }
        });
        manager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View item, int position) {
                //滑动到某一项的position
                Log.i(TAG, "onItemSelected: " + position);
            }
        });
        SampleAdapter sampleAdapter = new SampleAdapter(getSampleList());
        recyclerView.setAdapter(sampleAdapter);
    }

    private List<String> getSampleList() {
        List<String> sampleList = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            sampleList.add(i + "");
        }

        return sampleList;
    }
}
