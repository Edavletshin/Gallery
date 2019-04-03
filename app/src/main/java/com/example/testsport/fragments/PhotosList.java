package com.example.testsport.fragments;

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
import java.util.List;

import static com.example.testsport.MainActivity.CLIENT_ID;

public class PhotosList extends Fragment implements AbsListView.OnScrollListener  {

    private Unsplash unsplash;
    private StaggeredGridView mGridView;
    private ProgressBar progressBar;
    private SampleAdapter adapter;
    private List<Target> list = new ArrayList<>();

    //количество скаченных фоток
    private int count = 0;
    //количество картинок, которое должно скачаться
    private int totalCount = 0;
    //Разрешение для скачивания еще картинок при перелистывании
    private boolean mHasRequestedMore = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_list, container, false);
        unsplash = new Unsplash(CLIENT_ID);
        mGridView = view.findViewById(R.id.grid_view);
        progressBar = view.findViewById(R.id.progressbar);
        adapter  = new SampleAdapter(getActivity(), R.layout.item_photo, getActivity().getFragmentManager());
        adapter.setNotifyOnChange(true);

        loadData(30);

        mGridView.setAdapter(adapter);
        mGridView.setOnScrollListener(this);
        return view;
    }

    public void loadData(int countOfPhotos) {
        unsplash.getRandomPhotos("", false, "", "", "portrait", countOfPhotos, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(final List<Photo> photos) {
                totalCount += photos.size();

                for (final Photo photo : photos) {
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                            count++;
                            if (totalCount == count) {
                                //в этой точке скачано countOfPhotos картинок, и теперь если пользователь дошел до конца списка, то будет загрузка новых картинок
                                list.clear();
                                mHasRequestedMore = false;
                            }

                            Pojo pojo = new Pojo();

                            //рескейлинг картинкок, для занятия меньшего объема памяти
                            Matrix matrix = new Matrix();
                            if (bitmap.getWidth() < bitmap.getHeight()) {
                                matrix.postScale((float) 300 / bitmap.getWidth(), (float) 300 / bitmap.getWidth());
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
                            Log.e("photo_list", e.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    };
                    list.add(target);
                    Picasso.get().load(photo.getUrls().getRegular()).into(target);
                }
            }

            @Override
            public void onError(String error) {
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
                mHasRequestedMore = true;
                progressBar.setVisibility(View.VISIBLE);
                loadData(30);
            }
        }
    }

}
