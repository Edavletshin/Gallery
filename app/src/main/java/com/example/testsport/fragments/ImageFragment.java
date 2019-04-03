package com.example.testsport.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testsport.MainActivity;
import com.example.testsport.Pojo;
import com.example.testsport.R;
import com.example.testsport.SwipeBackLayout;
import com.example.testsport.adapters.SampleAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.example.testsport.MainActivity.PREFERENCES;
import static com.example.testsport.MainActivity.PREFERENCES_LIST;

public class ImageFragment extends Fragment {

    private View view;
    private Pojo pojo;
    private Menu menu;
    private FavouritesList fList;
    private SharedPreferences mSettings;
    private boolean downloaeded = false;

    public static ImageFragment getInstance(Pojo pojo) {
        ImageFragment fragment = new ImageFragment();
        fragment.pojo = pojo;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.imageshow, container,false);
        setHasOptionsMenu(true);

        fList = ((MainActivity)getActivity()).getFavouritesList();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");

        //восстанавливает черный фон
        view.findViewById(R.id.black).getBackground().setAlpha(255);

        //закрытие фрагмента по кнопке
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Picasso.get().load(pojo.getPhotoLink()).into(target);

        SwipeBackLayout swipeBackLayout = view.findViewById(R.id.swipe_layout);

        swipeBackLayout.setOnPullToBackListener(new SwipeBackLayout.SwipeBackListener() {
            @Override
            public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                //регулировка альфы бэкграунда, чтобы при прокрутке картинки менялся и задний фон
                view.findViewById(R.id.black).getBackground().setAlpha(255-((int) (fractionAnchor * 255)));
            }
        });


        return view;
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ((ImageView)view.findViewById(R.id.photo)).setImageBitmap(bitmap);
            view.findViewById(R.id.pBar).setVisibility(View.GONE);
            downloaeded = true;
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        mSettings = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (!mSettings.getString(PREFERENCES_LIST, "").contains(pojo.getId()))
            menu.findItem(R.id.addfavor).setTitle(getString(R.string.addfavor));
        else
            menu.findItem(R.id.addfavor).setTitle(getString(R.string.rmfavor));
        menu.findItem(R.id.author1).setTitle("Photo by " + pojo.getAuthor());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.author1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pojo.getLink()+"?utm_source=Yandex.Galery&utm_medium=referral")));
                return true;
            case R.id.unsplash:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/?utm_source=Yandex.Galery&utm_medium=referral")));
                return true;
            case R.id.addfavor:
                if (downloaeded) {
                    if (!mSettings.getString(PREFERENCES_LIST, "").contains(pojo.getId())) {
                        Toast.makeText(getActivity(), "Added!", Toast.LENGTH_SHORT).show();
                        mSettings.edit().putString(PREFERENCES_LIST, mSettings.getString(PREFERENCES_LIST, "") + " " + pojo.getId()).apply();
                        fList.getAdapter().add(pojo);
                        fList.getAdapter().notifyDataSetChanged();
                        menu.findItem(R.id.addfavor).setTitle(getString(R.string.rmfavor));
                        fList.getmView().findViewById(R.id.emptytext).setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Removed!", Toast.LENGTH_SHORT).show();
                        mSettings.edit().putString(PREFERENCES_LIST, mSettings.getString(PREFERENCES_LIST, "").replace(" " + pojo.getId(), "")).apply();
                        if (mSettings.getString(PREFERENCES_LIST, "").equals(""))
                            fList.getmView().findViewById(R.id.emptytext).setVisibility(View.VISIBLE);
                        fList.getAdapter().remove(pojo);
                        fList.getAdapter().notifyDataSetChanged();
                        menu.findItem(R.id.addfavor).setTitle(getString(R.string.addfavor));
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
