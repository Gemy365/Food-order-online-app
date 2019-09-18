package com.example.android.eatit.Interface;

import android.support.v7.widget.RecyclerView;

// InterFace To Swipe Order To Delete It From Cart.
public interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}
