package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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

    public static final String MOVIE_FILE = "movies.csv";
    public static final String MOVIE_GENRES_SPLIT = "\\|";

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
        genres = v[2].trim();
        Matcher yearMatcher = YEAR_PATTERN.matcher(v[1]);
        int begin = v[1].length();
        String t = "-1";
        while (yearMatcher.find()) {
            t = yearMatcher.group();
            begin = yearMatcher.start();
        }
        this.year = Integer.parseInt(t);
        this.title = v[1].substring(0, begin).trim();
    }

    @Override
    public int hashCode() {
        return movieId;
    }

    public static void buildMovieDb(ThreadPoolExecutor pool) {
        ParseFile.batchParse(COMMON_FILE_PATH + MOVIE_FILE, lst -> {
            Resource.batchInsert((dbWriter, lines) -> {
                List<Movie> movies = lines.stream().map(Movie::new).collect(Collectors.toList());
                dbWriter.insertMovies(movies);
            }, lst);
        }, pool);
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildMovieDb(pool);
        pool.shutdown();
    }
}
