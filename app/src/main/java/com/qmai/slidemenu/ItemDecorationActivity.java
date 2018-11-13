package com.qmai.slidemenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qmai.slidemenu.decoration.GridDividerItemDecoration;
import com.qmai.slidemenu.decoration.LinearDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemDecorationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.ItemDecoration itemDecoration;

    private List<String> data = new ArrayList<>();
    private SlideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_decoration);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initData();
        showGrid(GridLayoutManager.VERTICAL);
        adapter = new SlideAdapter(this, recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setData(data);

        //交互动画
        MyItemTouchHelperCallback callback = new MyItemTouchHelperCallback(new MyItemTouchHelperCallback.ItemSwipeListener() {
            @Override
            public void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction) {
                //删除数据 更新列表
                int position = viewHolder.getAdapterPosition();
                data.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onItemDrag(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //交换数据 更新列表
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(data, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
            }
        });
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            data.add("测试数据" + i);
        }
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037235_3453.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037235_9280.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037234_3539.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037234_6318.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037194_2965.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037193_1687.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037193_1286.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037192_8379.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037178_9374.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037177_1254.jpg");
//
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037177_6203.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037152_6352.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037151_9565.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037151_7904.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037148_7104.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037129_8825.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037128_5291.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037128_3531.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037127_1085.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037095_7515.jpg");
//
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037094_8001.jpg");
//        data.add("http://img.my.csdn.net/uploads/201309/01/1378037093_7168.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949643_6410.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949642_6939.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949630_4505.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949630_4593.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949629_7309.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949629_8247.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949615_1986.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949614_8482.jpg");
//        data.add("http://img.my.csdn.net/uploads/201308/31/1377949614_3743.jpg");
//        /*"http://img.my.csdn.net/uploads/201309/01/1378037235_3453.jpg",
//
//			"http://img.my.csdn.net/uploads/201308/31/1377949614_4199.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949599_3416.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949599_5269.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949598_7858.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949598_9982.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949578_2770.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949578_8744.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949577_5210.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949577_1998.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949482_8813.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949481_6577.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949480_4490.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949455_6792.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949455_6345.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949442_4553.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949441_8987.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949441_5454.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949454_6367.jpg",
//			"http://img.my.csdn.net/uploads/201308/31/1377949442_4562.jpg"*/
    }

    public void showLinear(@RecyclerView.Orientation int orientation) {
        if(itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new LinearDividerItemDecoration(this, orientation);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
    }

    public void showGrid(@RecyclerView.Orientation int orientation) {
        if(itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new GridDividerItemDecoration(this);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, orientation, false));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
                showGrid(GridLayoutManager.VERTICAL);
                recyclerView.setAdapter(adapter);
                adapter.setData(data);
                break;
            case R.id.button3:
                showGrid(GridLayoutManager.HORIZONTAL);
                recyclerView.setAdapter(adapter);
                adapter.setData(data);
                break;
            case R.id.button4:
                showLinear(LinearLayoutManager.VERTICAL);
                recyclerView.setAdapter(adapter);
                adapter.setData(data);
                break;
            case R.id.button5:
                showLinear(LinearLayoutManager.HORIZONTAL);
                recyclerView.setAdapter(adapter);
                adapter.setData(data);
                break;
            case R.id.button6:

                break;
            case R.id.button7:

                break;
        }
    }
}
