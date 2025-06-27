package com.example.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobile.Models.Order;
import com.example.mobile.OrderDetailActivity;
import com.example.mobile.R;

import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {
    private Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item_order, parent, false);
        }

        Order order = getItem(position);

        TextView orderIdTextView = listItem.findViewById(R.id.order_id);
        TextView dateTextView = listItem.findViewById(R.id.order_date);
        TextView nameTextView = listItem.findViewById(R.id.order_customer_name);
        TextView amountTextView = listItem.findViewById(R.id.order_amount);
        Button detailButton = listItem.findViewById(R.id.btn_order_detail); // Use listItem instead of convertView

        orderIdTextView.setText("Order ID: " + order.getOrderID());
        dateTextView.setText("Date: " + order.getOrderDate());
        nameTextView.setText("Customer: " + order.getCustomerName());
        amountTextView.setText("Total: $" + String.format("%.2f", order.getTotalAmount()));

        detailButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", order.getOrderID());
            context.startActivity(intent);
        });

        return listItem;
    }
}