package com.example.testsport;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.testsport.api.UnsplashApi;
import com.example.testsport.models.Photo;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Unsplash implements Interceptor {

    private static final String BASE_URL = "https://api.unsplash.com/";
    private String TAG = "Unsplash";

    private UnsplashApi api;
    private String clientId;

    public Unsplash(String clientId) {
        this.clientId = clientId;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(this).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(UnsplashApi.class);
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("Authorization", "Client-ID " + clientId)
                .build();
        return chain.proceed(request);
    }

    public void getRandomPhotos(@Nullable String collections,
                                @Nullable Boolean featured, @Nullable String username,
                                @Nullable String query, @Nullable String orientation,
                                @Nullable Integer count, OnPhotosLoadedListener listener) {
        Call<List<Photo>> call = api.getRandomPhotos(collections, featured, username, query, orientation, count);
        call.enqueue(getMultiplePhotoCallback(listener));
    }

    public void getPhoto(@NonNull String id, final OnPhotoLoadedListener listener) {
        Call<Photo> call = api.getPhoto(id);
        call.enqueue(getSinglePhotoCallback(listener));
    }

    private Callback<List<Photo>> getMultiplePhotoCallback(final OnPhotosLoadedListener listener) {
        return new UnsplashCallback<List<Photo>>() {
            @Override
            void onComplete(List<Photo> response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<List<Photo>> call, String message) {
                Log.d(TAG, "URL - " + call.request().url());
                listener.onError(message);
            }
        };
    }

    private Callback<Photo> getSinglePhotoCallback(final OnPhotoLoadedListener listener) {
        return new UnsplashCallback<Photo>() {
            @Override
            void onComplete(Photo response) {
                listener.onComplete(response);
            }

            @Override
            void onError(Call<Photo> call, String message) {
                Log.d(TAG, "URL - " + call.request().url());
                listener.onError(message);
            }
        };
    }

    private abstract class UnsplashCallback<T> implements Callback<T> {

        abstract void onComplete(T response);

        abstract void onError(Call<T> call, String message);

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            int statusCode = response.code();
            Log.d(TAG, "Статус - " + statusCode);
            if (statusCode == 200) {
                onComplete(response.body());
            } else if (statusCode >= 400) {
                onError(call, String.valueOf(statusCode));

                if (statusCode == 401) {
                    Log.d(TAG, "Неправильый id");
                }
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            onError(call, t.getMessage());
        }
    }

    public interface OnPhotosLoadedListener {
        void onComplete(List<Photo> photos);

        void onError(String error);
    }

    public interface OnPhotoLoadedListener {
        void onComplete(Photo photo);

        void onError(String error);
    }
}
