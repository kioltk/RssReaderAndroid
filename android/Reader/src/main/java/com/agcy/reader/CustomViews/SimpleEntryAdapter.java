package com.agcy.reader.CustomViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.agcy.reader.Helpers.Appearance;
import com.agcy.reader.Models.Feedly.Entry;
import com.agcy.reader.R;

import java.util.Collections;
import java.util.List;

public class SimpleEntryAdapter extends BaseAdapter {

    private List<Entry> items = Collections.emptyList();

    private int lastSlided =-1;
    private final Context context;

    // the context is needed to inflate views in getView()
    public SimpleEntryAdapter(Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.i("agcylog", "начинаем отображение элемента");
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.simple_entry, parent, false);

        SuperImageView imageView = (SuperImageView) rootView.findViewById(R.id.image);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView dateView = (TextView) rootView.findViewById(R.id.date);
        TextView originView = (TextView) rootView.findViewById(R.id.origin);

        Log.i("agcylog","схвачены все группы отображений");
        Entry entryItem = getItem(position);

        Log.i("agcylog","статья найдена, заносим данные в элемент");
        try{
            titleView.setText(entryItem.title);
            String content = entryItem.summary.content;
            //contentView.setText(content);



            if (entryItem.visual.url!=null)
                imageView.setImageUrl(entryItem.visual.url);
            else
                imageView.setVisibility(View.GONE);
            dateView.setText(entryItem.dateLocale());
            originView.setText(entryItem.feed().title);
            final View speakerView = rootView.findViewById(R.id.readButton);
            ((ImageButton)speakerView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "TODO:Сча подам голос!", Toast.LENGTH_SHORT).show();
                    // TODO: Voice
                }
            });
        } catch (Exception exp){
            Log.e("agcylog","плохая статья(");
            titleView.setText("плохая статья");
        }
        /*
        * */
        Log.i("agcylog","возвращаем элемент");

        if(position>lastSlided){
            Appearance.slideFromBottom(rootView);
        }else{
            Appearance.slideFromTop(rootView);
        }

        lastSlided = position;
        return rootView;
    }


}
