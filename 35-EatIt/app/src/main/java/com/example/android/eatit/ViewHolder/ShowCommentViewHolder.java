package com.example.android.eatit.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.eatit.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserPhone, txtComment;
    public RatingBar rateBar;

    public ShowCommentViewHolder(View itemView) {
        super(itemView);

        txtUserPhone = (TextView) itemView.findViewById(R.id.txt_user_phone);
        txtComment = (TextView) itemView.findViewById(R.id.txt_comment);
        rateBar = (RatingBar) itemView.findViewById(R.id.rate_bar);
    }
}
