package com.example.android.eatit.Interface;

import android.view.View;

// Use ItemClickListener InterFace Determine The Image And Know The Position Of Image.
public interface ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
