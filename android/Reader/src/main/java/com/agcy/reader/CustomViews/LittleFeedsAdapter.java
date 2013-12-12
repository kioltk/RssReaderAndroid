package com.agcy.reader.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kiolt_000 on 07.12.13.
 */
public class LittleFeedsAdapter extends BaseAdapter {
    private List<Feed> items = Collections.emptyList();
    Context context;
    public LittleFeedsAdapter(Context context){
       this.context = context;
    }
    public static AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.little_feed, parent, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.feedName);
        TextView countView = (TextView) rootView.findViewById(R.id.unreadCount);
        Feed item = (Feed) getItem(position);

        titleView.setText(item.title);
        countView.setText(String.valueOf(item.unreadCount()));

        return rootView;
    }

    public void updateItems(ArrayList<Feed> feeds) {
        items = feeds;
        notifyDataSetChanged();
    }
}
