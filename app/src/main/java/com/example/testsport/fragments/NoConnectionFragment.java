package com.example.testsport.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testsport.MainActivity;
import com.example.testsport.R;

public class NoConnectionFragment extends Fragment {

    private MainActivity main;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_connection_layout, container, false);

        main = (MainActivity)getActivity();

        view.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (main.isOnline())
                {
                    main.getPhotosList().loadData(30);
                    main.getFavouritesList().init();
                    main.getSupportFragmentManager().beginTransaction().remove(NoConnectionFragment.this).commit();
                }
             }
        });

        return view;
    }

}
