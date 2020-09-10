package com.qmai.crashlib.tool;

import android.view.View;

/**
 * <pre>
 *     @author kathline
 *     time  : 2020/7/10
 *     desc  : 点击事件
 *     revise:
 * </pre>
 */
public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onLongClick(View view, int position);
}
