package com.agcy.reader.CustomViews;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.agcy.reader.Helpers.Appearance;
import com.agcy.reader.Models.Feedly.Category;
import com.agcy.reader.Models.Feedly.Feed;
import com.agcy.reader.R;

import java.util.Collections;
import java.util.List;

import static com.agcy.reader.Helpers.ExpandCollapse.collapseVertical;
import static com.agcy.reader.Helpers.ExpandCollapse.expandVertical;

/**
 * Created by kiolt_000 on 27.11.13.
 */
public class SimpleCategoryAdapter extends BaseAdapter {


    private List<Category> items = Collections.emptyList();

    private AdapterView.OnItemClickListener itemClickListener;
    private Context context;
    public LayoutInflater inflater;
    public Activity activity;
    private int lastSlided = -1;

    public void setOnFeedClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        itemClickListener = onItemClickListener;
    }


    /**
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).feeds().get(childPosition);// groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.category_simple_feed_item, parent, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.feedName);
        Feed item = (Feed) getChild(groupPosition,childPosition);

        titleView.setText(item.title);


        return rootView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).feeds().size();
    }
    */
    @Override
    public Object getItem(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getCount() {
        return items.size();
    }
    /*
    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }*/



    @Override
    public View getView(final int position,
                             View convertView, final ViewGroup parent) {

        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.simple_category, parent, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView unreadCountView = (TextView) rootView.findViewById(R.id.unreadCount);
        ImageButton showFeeds = (ImageButton) rootView.findViewById(R.id.showFeeds);
        final View contentView =  rootView.findViewById(R.id.contentView);
        final ListView categoryFeeds = (ListView) rootView.findViewById(R.id.categoryFeeds);
        Category item = (Category) getItem(position);
        /*showFeeds.setOnClickListener(new View.OnClickListener() {
            final View toShow = categoryFeeds;
            @Override
            public void onClick(View v) {
                if(categoryFeeds.getVisibility()==View.GONE){

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_to_bottom);

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            toShow.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    toShow.startAnimation(animation);

                }
                else{

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_to_top);

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            toShow.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    toShow.startAnimation(animation);

                }
            }
        });*/
        showFeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("agcylog", "кликнут элемент");

                if(contentView.getVisibility()==View.GONE){
                    expandVertical(contentView);
                }
                else {
                    collapseVertical(contentView);

                }
            }
        });
        String[] feedsNames = new String[item.feeds().size()];
        for(int i = 0; i<item.feeds().size();i++){
            Feed feed = item.feeds().get(i);
            feedsNames[i] = feed.title;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        int height = (int) (feedsNames.length * 30 * scale + 0.5f + 2);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        categoryFeeds.setLayoutParams(params);
        /*
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(feedsNames.length*30, View.MeasureSpec.AT_MOST);
        categoryFeeds.measure(widthSpec, heightSpec);
        */
        LittleFeedsAdapter feedsAdapter = new LittleFeedsAdapter(context);
        feedsAdapter.updateItems(item.feeds());
        categoryFeeds.setAdapter(feedsAdapter);
        categoryFeeds.setOnItemClickListener((AdapterView.OnItemClickListener) itemClickListener);
        titleView.setText(item.label);
        unreadCountView.setText(String.valueOf( item.unreadCount()));
        //((ExpandableListView) parent).expandGroup(groupPosition);

        if(position>=lastSlided){
            Appearance.slideFromBottom(rootView);
        }else{

            Appearance.slideFromTop(rootView);

        }

        lastSlided = position;
        return rootView;
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getId(int position) {
       return ((Category) getItem(position)).id;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }





    // the context is needed to inflate views in getView()
    public SimpleCategoryAdapter(Context context) {
        this.context = context;
    }

    public void updateItems(List<Category> items) {
        this.items = items;
        notifyDataSetChanged();

    }



    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance


    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter





}