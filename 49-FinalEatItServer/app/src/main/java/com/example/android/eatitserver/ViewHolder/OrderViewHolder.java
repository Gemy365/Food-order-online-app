package com.example.android.eatitserver.ViewHolder;


import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderTotal, getTxtOrderDate;

    public Button btnEdit, btnRemove, btnDetail, btnDirection;

    public OrderViewHolder(View itemView) {
        // Extends From RecyclerView.ViewHolder.
        super(itemView);

        // Get IDs To Make View Of Texts.
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderTotal = (TextView) itemView.findViewById(R.id.order_total);
        getTxtOrderDate = (TextView) itemView.findViewById(R.id.order_date);

        btnEdit = (Button) itemView.findViewById(R.id.btn_edit);
        btnRemove = (Button) itemView.findViewById(R.id.btn_remove);
        btnDetail = (Button) itemView.findViewById(R.id.btn_detail);
       // btnDirection = (Button) itemView.findViewById(R.id.btn_direction);
    }
}
