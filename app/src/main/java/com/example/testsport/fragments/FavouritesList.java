package com.example.testsport.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.example.testsport.MainActivity;
import com.example.testsport.Pojo;
import com.example.testsport.R;
import com.example.testsport.Unsplash;
import com.example.testsport.adapters.SampleAdapter;
import com.example.testsport.models.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.testsport.MainActivity.CLIENT_ID;
import static com.example.testsport.MainActivity.PREFERENCES;
import static com.example.testsport.MainActivity.PREFERENCES_LIST;

public class FavouritesList extends Fragment implements AbsListView.OnScrollListener {

    private Unsplash unsplash;
    private ProgressBar progressBar;
    private SampleAdapter adapter;
    private List<String> list;
    private List<Target> temp_list = new ArrayList<>();
    private View mView;
    private SharedPreferences mSettings;

    private int currentItem = 0;
    private int total = 30;
    private boolean mHasRequestedMore = true;

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.favorites_list, container, false);

        mSettings = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        progressBar = mView.findViewById(R.id.progressbar);
        StaggeredGridView mGridView =  mView.findViewById(R.id.grid_view);

        unsplash = new Unsplash(CLIENT_ID);
        adapter  = new SampleAdapter(getActivity(), R.layout.item_photo, getActivity().getFragmentManager());
        adapter.setNotifyOnChange(true);
        //очистка избранного
        mView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Clear!", Toast.LENGTH_SHORT).show();
                mSettings.edit().remove(PREFERENCES_LIST).apply();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        });

        init();

        mGridView.setAdapter(adapter);
        mGridView.setOnScrollListener(this);
        return mView;
    }

    public SampleAdapter getAdapter() {
        return adapter;
    }

    public View getmView() {
        return mView;
    }

    public void init()
    {
        String[] items = mSettings.getString(PREFERENCES_LIST, "").split(" ");
        list = new ArrayList<>(Arrays.asList(items));
        list.remove(0);

        if (list.size() != 0 && ((MainActivity)getActivity()).isOnline())
        {
            mView.findViewById(R.id.emptytext).setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            loadData();
        }
    }

    public void loadData(){
            unsplash.getPhoto(list.get(currentItem), new Unsplash.OnPhotoLoadedListener() {
                @Override
                public void onComplete(final Photo photo) {

                    //загрузка след картинки
                    currentItem++;
                    if(currentItem < list.size())
                        loadData();

                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Pojo pojo = new Pojo();

                            if (currentItem == total){
                                //в этой точке скачано countOfPhotos картинок, и теперь если пользователь дошел до конца списка, то будет загрузка новых картинок
                                temp_list.clear();
                                mHasRequestedMore=false;
                            }

                            //рескейлинг картинкок, для занятия меньшего объема памяти
                            Matrix matrix = new Matrix();
                            if (bitmap.getWidth()<bitmap.getHeight()) {
                                matrix.postScale((float) 300 / bitmap.getWidth(), (float)  300 / bitmap.getWidth());
                                pojo.setBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                            } else {
                                matrix.postScale((float) 300 / bitmap.getHeight(), (float) 300 / bitmap.getHeight());
                                pojo.setBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            pojo.setLink(photo.getUser().getLinks().getHtml());
                            pojo.setAuthor(photo.getUser().getName());
                            pojo.setPhotoLink(photo.getUrls().getRegular());
                            pojo.setId(photo.getId());
                            adapter.add(pojo);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e("favorite_list", e.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };
                    temp_list.add(target);
                    Picasso.get().load(photo.getUrls().getRegular()).into(target);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

        //слушатель на промотку списка, для загрузки новых картинок

        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount ) {
                total += 30;
                mHasRequestedMore = true;
                progressBar.setVisibility(View.VISIBLE);
                loadData();
            }
        }
    }

}