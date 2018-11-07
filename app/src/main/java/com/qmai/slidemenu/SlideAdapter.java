package com.qmai.slidemenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.MyViewHolder> {
    private List<String> list_data = new ArrayList<>();
    private Context mContext;

    public SlideAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public SlideAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_swipe, viewGroup, false);
        SlideAdapter.MyViewHolder holder = new SlideAdapter.MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SlideAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.tv_title.setText(list_data.get(position));
//        myViewHolder.view_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int n =  myViewHolder.getLayoutPosition();
//                Log.e("---=","delete");
//                removeData(n);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView view_del;
        TextView view_edit;
        TextView tv_top;
        ImageView iv_avatar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.item_tv_title);
//            tv_top = (TextView) itemView.findViewById(R.id.item_tv_top);
//
//            view_del = (TextView) itemView.findViewById(R.id.item_tv_del);
//            view_edit = (TextView) itemView.findViewById(R.id.item_tv_edit);
            iv_avatar = (ImageView) itemView.findViewById(R.id.item_avatar);
        }
    }

    public void setData(List<String> list) {
        if(list != null) {
            list_data.clear();
            list_data.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void removeData(int position){
        list_data.remove(position);
        notifyItemRemoved(position);

    }

}
