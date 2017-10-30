package com.android.popmoviestwo;

/**
 * Created by mmalla on 25/10/17.
 */

public class Movie {

    private String movieTitle;
    private String movieImgPath;
    private String movieId;
    private String releaseDate;
    private String overview;
    private String userRating;

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

}
