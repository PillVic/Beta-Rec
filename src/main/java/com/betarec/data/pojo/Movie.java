package com.betarec.data.pojo;

import com.betarec.Base;

import java.util.regex.Matcher;

import static com.betarec.utils.Flags.*;

/**
 * parse movie.csv: movieId, title, genres
 *
 * @author pillvic
 * @date 22-01/06
 */
public class Movie extends Base {
    public final int movieId;
    public final String title;
    public final String genres;
    public final int year;

    public static final String MOVIE_GENRES_SPLIT = "\\|";
    public static final String EMPTY_GENRES = "(no genres listed)";

    public Movie(int movieId, String title, String genres, int year) {
        this.movieId = movieId;
        this.title = title;
        this.genres = genres;
        this.year = year;
    }

    public Movie(String line) {
        String[] v;
        if (titleContainComma(line)) {
            v = line.split("(,\")|(\",)");
        } else {
            v = line.split(",");
        }
        movieId = Integer.parseInt(v[0]);
        if (EMPTY_GENRES.equals(v[2].trim())) {
            genres = "";
        } else {
            genres = v[2].trim();
        }
        Matcher yearMatcher = YEAR_PATTERN.matcher(v[1]);
        int begin = v[1].length();
        if (yearMatcher.find()) {
            String yearStr = yearMatcher.group();
            this.year = Integer.parseInt(yearStr.substring(1, 5));
            this.title = v[1].substring(0, v[1].indexOf(yearStr)).trim();
        } else {
            this.year = -1;
            this.title = v[1].substring(0, begin).trim();
        }
    }

    @Override
    public int hashCode() {
        return movieId;
    }
}
