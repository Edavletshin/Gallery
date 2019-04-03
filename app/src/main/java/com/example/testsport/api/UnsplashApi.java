package com.example.testsport.api;

import com.example.testsport.models.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UnsplashApi {

    @GET("photos/{id}")
    Call<Photo> getPhoto(@Path("id") String id);

    @GET("photos/random")
    Call<List<Photo>> getRandomPhotos(@Query("collections") String collections,
                                      @Query("featured") boolean featured, @Query("username") String username,
                                      @Query("query") String query, @Query("orientation") String orientation,
                                      @Query("count") Integer count);
}
