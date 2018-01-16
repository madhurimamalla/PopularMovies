package com.android.popmoviestwo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by mmalla on 25/10/17.
 */

public class Movie implements Parcelable{

    private String movieTitle;
    private String movieImgPath;
    private String movieId;
    private String releaseDate;
    private String overview;
    private String userRating;

    /**
     * There can be more than one review
     */
    private List<String> moviereviewsList;

    /**
     * There can be more than one video link
     */
    private List<String> movieTrailerList;

    /**
     * Constructor here
     *
     * @param name
     * @param movieImgPath
     * @param movieId
     */
    public Movie(String name, String movieImgPath, String movieId) {
        this.movieTitle = name;
        this.movieImgPath = movieImgPath;
        this.movieId = movieId;
    }

    public Movie(Parcel source) {
        movieTitle = source.readString();
        movieId = source.readString();
        movieImgPath = source.readString();
        releaseDate = source.readString();
        overview = source.readString();
        userRating = source.readString();
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieImgPath() {
        return movieImgPath;
    }

    public void setMovieImgPath(String movieImgPath) {
        this.movieImgPath = movieImgPath;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public List<String> getMoviereviewsList() {
        return moviereviewsList;
    }

    public void setMoviereviewsList(List<String> moviereviewsList) {
        this.moviereviewsList = moviereviewsList;
    }

    public List<String> getMovieTrailerList() {
        return movieTrailerList;
    }

    public void setMovieTrailerList(List<String> movieTrailerList) {
        this.movieTrailerList = movieTrailerList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(movieId);
        dest.writeString(movieImgPath);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeString(userRating);
//        dest.writeList(moviereviewsList);
//        dest.writeList(movieTrailerList);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
