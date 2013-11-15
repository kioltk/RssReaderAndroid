package com.agcy.reader.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agcy.reader.Models.RssItem;
import com.agcy.reader.R;
import com.loopj.android.image.SmartImageView;

import java.util.Collections;
import java.util.List;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class RssListAdapter extends BaseAdapter {

    private List<RssItem> items = Collections.emptyList();

    private final Context context;

    // the context is needed to inflate views in getView()
    public RssListAdapter(Context context) {
        this.context = context;
    }

    public void updateItems(List<RssItem> bananaPhones) {
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
    public RssItem getItem(int position) {
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

        RssItem rssItem = getItem(position);

        titleView.setText(rssItem.title);
        descriptionView.setText(rssItem.description);
        imageView.setImageUrl(rssItem.image);
        dateView.setText(rssItem.pubDate);
        linkView.setText(rssItem.link);
        return rootView;
    }


}