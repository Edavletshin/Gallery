
package com.example.testsport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResults implements Parcelable
{

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<Photo> results = null;
    public final static Creator<SearchResults> CREATOR = new Creator<SearchResults>() {


        @SuppressWarnings({
            "unchecked"
        })
        public SearchResults createFromParcel(Parcel in) {
            SearchResults instance = new SearchResults();
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (Photo.class.getClassLoader()));
            return instance;
        }

        public SearchResults[] newArray(int size) {
            return (new SearchResults[size]);
        }

    }
    ;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Photo> getResults() {
        return results;
    }

    public void setResults(List<Photo> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(total);
        dest.writeValue(totalPages);
        dest.writeList(results);
    }

    public int describeContents() {
        return  0;
    }

}
