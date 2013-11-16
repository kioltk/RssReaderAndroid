package com.agcy.reader.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.R;
import com.loopj.android.image.SmartImageView;

import java.util.Collections;
import java.util.List;

public class entrySimpleItemAdapter extends BaseAdapter {

    private List<Entry> items = Collections.emptyList();

    private final Context context;

    // the context is needed to inflate views in getView()
    public entrySimpleItemAdapter(Context context) {
        this.context = context;
    }

    public void updateItems(List<Entry> bananaPhones) {
        this.items = bananaPhones;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public Entry getItem(int position) {
        return items.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.rss_list_item, parent, false);

        SmartImageView imageView = (SmartImageView) rootView.findViewById(R.id.image);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView descriptionView = (TextView) rootView.findViewById(R.id.description);
        TextView dateView = (TextView) rootView.findViewById(R.id.date);
        TextView linkView = (TextView) rootView.findViewById(R.id.link);

        Entry entryItem = getItem(position);

        titleView.setText(entryItem.title);
        descriptionView.setText(entryItem.summary.content);
        if (entryItem.visual!=null)
            imageView.setImageUrl(entryItem.visual.url);
        dateView.setText(String.valueOf(entryItem.published));
        linkView.setText(entryItem.originId);
        return rootView;
    }


}
