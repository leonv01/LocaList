package com.mad.localist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GroceryListAdapter extends BaseAdapter {

    List<GroceryEntry> data;

    public GroceryListAdapter(List<GroceryEntry> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Context context = parent.getContext();
        GroceryEntry groceryEntry = data.get(position);

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grocery_list_entry, null, false);
        }

        TextView nameTextView = view.findViewById(R.id.grocery_list_entry_name);
        TextView amountTextView = view.findViewById(R.id.grocery_list_entry_amount);
        nameTextView.setText(groceryEntry.getName());
        amountTextView.setText(groceryEntry.getQuantity());

        return view;
    }
}
