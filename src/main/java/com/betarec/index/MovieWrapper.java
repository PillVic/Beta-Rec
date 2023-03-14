package com.betarec.index;

import gen.data.pojo.GenomeScore;
import gen.data.pojo.Movie;
import gen.data.pojo.Rating;
import org.apache.lucene.document.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.betarec.utils.Flags.EMPTY_GENRES;
import static com.betarec.utils.Flags.MOVIE_GENRES_SPLIT;


public class MovieWrapper {
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_YEAR = "movie_year";
    public static final String MOVIE_GENRES = "movie_year";
    public static final String MOVIE_GENRES_ALL = "movie_genres_all";
    public static final String MOVIE_AVERAGE_RATING = "movie_average_rating";
    public static final String MOVIE_AUDIENCE_COUNT = "movie_audience_count";
    /**
     * movieTag的保存通过前缀：tagId存储，值为相关度,
     * 从而既可查询有无tag,也可查询相关度大小
     */
    public static final String MOVIE_TAG_PREFIX = "movie_tag:";

    public Movie movie;
    public List<GenomeScore> genomeScores;
    public List<Rating> ratings;

    public MovieWrapper(Movie movie) {
        this.movie = movie;
    }

    public MovieWrapper setGenomeScores(List<GenomeScore> genomeScores) {
        this.genomeScores = genomeScores;
        return this;
    }

    public MovieWrapper setRatings(List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    public Document getDoc() {
        Document doc = new Document();
        doc.add(new StringField(MOVIE_ID, movie.movieId + "", Field.Store.YES));
        doc.add(new IntPoint(MOVIE_YEAR, movie.year));
        if (StringUtils.hasLength(movie.genres)) {
            List<String> genres = Arrays
                    .stream(movie.genres.split(MOVIE_GENRES_SPLIT)).toList();
            genres.forEach(gen -> {
                doc.add(new StringField(MOVIE_GENRES, gen, Field.Store.NO));
            });
            doc.add(new StringField(MOVIE_GENRES_ALL, movie.genres, Field.Store.YES));
        } else {
            doc.add(new StringField(MOVIE_GENRES, EMPTY_GENRES, Field.Store.NO));
        }
        genomeScores.forEach(genomeScore -> {
            doc.add(new DoubleDocValuesField(MOVIE_TAG_PREFIX + genomeScore.tagId, genomeScore.relevance));
        });

        //todo
        //rating相关特征还没有写， 可以补充一些统计数据，作为非个性化推荐的特征指标
        if (CollectionUtils.isEmpty(ratings)) {
            doc.add(new IntPoint(MOVIE_AUDIENCE_COUNT, 0));
        } else {
            doc.add(new IntPoint(MOVIE_AUDIENCE_COUNT, ratings.size()));
            doc.add(new DoubleDocValuesField(MOVIE_AVERAGE_RATING, ratings.stream().collect(Collectors.averagingDouble(t -> t.rating))));
        }
        return doc;
    }

}
