package com.betarec.index;

import gen.data.pojo.Movie;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.document.*;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.betarec.utils.Flags.EMPTY_GENRES;
import static com.betarec.utils.Flags.MOVIE_GENRES_SPLIT;

/**
 * -1 means no seen movie
 */
public class UserWrapper {
    public static final String USER_ID = "user_id";
    public static final String SEEN_MOVIES = "seen_movies";
    public static final String SEEN_GENRES = "seen_genres";
    public static final String SEEN_GENRES_ALL = "seen_genres_all";
    public static final String SEEN_COUNT = "seen_count";


    public final int userId;
    public List<Movie> seenMovies;

    public UserWrapper(int userId) {
        this.userId = userId;
    }

    public UserWrapper setSeenMovies(List<Movie> movies) {
        this.seenMovies = movies;
        return this;
    }

    public Document getDoc() {
        Document doc = new Document();
        doc.add(new StringField(USER_ID, userId + "", Field.Store.YES));

        if (CollectionUtils.isNotEmpty(seenMovies)) {
            Set<String> genres = new HashSet<>();
            for (Movie movie : seenMovies) {
                doc.add(new IntPoint(SEEN_MOVIES, movie.movieId));
                if (StringUtils.hasLength(movie.genres)) {
                    genres.addAll(Arrays.stream(movie.genres.split(MOVIE_GENRES_SPLIT)).toList());
                }
            }
            if (CollectionUtils.isEmpty(genres)) {
                doc.add(new StringField(SEEN_GENRES, EMPTY_GENRES, Field.Store.YES));
            } else {
                genres.forEach(g -> {
                    doc.add(new StringField(SEEN_GENRES, g, Field.Store.YES));
                });
                doc.add(new StringField(SEEN_GENRES_ALL, String.join(MOVIE_GENRES_SPLIT, genres), Field.Store.YES));
            }
            doc.add(new IntPoint(SEEN_COUNT, seenMovies.size()));
        } else {
            doc.add(new IntPoint(SEEN_MOVIES, -1));
        }
        return doc;
    }
}
