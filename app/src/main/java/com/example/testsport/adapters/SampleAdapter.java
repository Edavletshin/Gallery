package com.example.testsport.adapters;


import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testsport.Pojo;
import com.example.testsport.R;
import com.example.testsport.fragments.FavouritesList;
import com.example.testsport.fragments.ImageFragment;


public class SampleAdapter extends ArrayAdapter<Pojo> {

    private FragmentManager manager;
    private final LayoutInflater mLayoutInflater;


    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        View wholeView;
    }

    public SampleAdapter(final Context context, final int textViewResourceId, FragmentManager manager) {
        super(context, textViewResourceId);
        mLayoutInflater = LayoutInflater.from(context);
        this.manager = manager;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_photo, parent, false);
            vh = new ViewHolder();
            vh.imageView = convertView.findViewById(R.id.imageView);
            vh.textView = convertView.findViewById(R.id.author);
            vh.wholeView = convertView;

            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }


        vh.imageView.setImageBitmap(getItem(position).getBitmap());
        //автор картинки
        vh.textView.setText(getItem(position).getAuthor());

        Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(1000);
        if (getCount()-1==position){
            //при появлении картинка появляется не резко, а плавно
            vh.wholeView.startAnimation(fadeInAnimation);
        }

        vh.wholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //увеличение картинки
                manager.beginTransaction().replace(R.id.frame, ImageFragment.getInstance(getItem(position))).addToBackStack("").commit();
            }
        });

        return convertView;
    }





}