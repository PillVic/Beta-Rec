package com.betarec.pojo;

import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.regex.Matcher;

import static com.betarec.utils.Flags.*;

/**
 * parse movie.csv: movieId, title, genres
 *
 * @author pillvic
 * @date 22-01/06
 */
public class Movie {
    public final int movieId;
    public final String title;
    public final String genres;
    public final int year;

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
        genres = v[2];
        Matcher yearMatcher = YEAR_PATTERN.matcher(v[1]);
        int begin = v[1].length();
        String t = "-1";
        while (yearMatcher.find()) {
            t = yearMatcher.group();
            begin = yearMatcher.start();
        }
        this.year = Integer.parseInt(t);
        this.title = v[1].substring(0, begin - 1);
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH + "movies.csv", line -> {
            Movie movie = new Movie(line);
            dbWriter.insertMovie(movie);
        });
    }
}
