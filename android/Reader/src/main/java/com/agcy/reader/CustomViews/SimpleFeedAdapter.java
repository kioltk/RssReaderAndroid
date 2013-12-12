package com.agcy.reader.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agcy.reader.Helpers.Appearance;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by kiolt_000 on 02.11.13.
 */
public class SimpleFeedAdapter extends BaseAdapter {

    private List<Feed> items = Collections.emptyList();

    private final Context context;
    private int lastSlided = -1;

    // the context is needed to inflate views in getView()
    public SimpleFeedAdapter(Context context) {
        this.context = context;
    }

    public void updateItems(List<Feed> feeds) {
        this.items = feeds;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public Feed getItem(int position) {
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
                .inflate(R.layout.simple_feed, parent, false);

        SuperImageView imageView = (SuperImageView) rootView.findViewById(R.id.icon);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView unreadCountView = (TextView) rootView.findViewById(R.id.unreadCount);
        Feed rssItem = getItem(position);

        titleView.setText(rssItem.title);
        imageView.setImageUrl(rssItem.icon());
        unreadCountView.setText(String.valueOf(rssItem.unreadCount()));

        if(position>=lastSlided ){
            Appearance.slideFromBottom(rootView);
        }else{

            Appearance.slideFromTop(rootView);
        }

        lastSlided = position;
        return rootView;
    }


}
