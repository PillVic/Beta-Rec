package com.betarec.index;

import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.Movie;
import com.betarec.pojo.Rating;
import org.apache.lucene.document.*;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class MovieWrapper {
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_YEAR = "movie_year";
    public static final String MOVIE_GENRES = "movie_year";
    /**
     *  movieTag的保存通过前缀：tagId存储，值为相关度,
     *  从而既可查询有无tag,也可查询相关度大小
    * */
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
        doc.add(new IntPoint(MOVIE_ID, movie.movieId));
        if (movie.year > 0) {
            doc.add(new IntPoint(MOVIE_YEAR, movie.year));
        }
        if (StringUtils.hasLength(movie.genres)) {
            List<String> genres = Arrays
                    .stream(movie.genres.split(Movie.MOVIE_GENRES_SPLIT)).toList();
            genres.forEach(gen -> {
                doc.add(new StringField(MOVIE_GENRES, gen, Field.Store.NO));
            });
        }
        genomeScores.forEach(genomeScore -> {
            doc.add(new DoubleDocValuesField(MOVIE_TAG_PREFIX + genomeScore.tagId, genomeScore.relevance));
        });
        //todo
        //rating相关特征还没有写， 可以补充一些统计数据，作为非个性化推荐的特征指标
        return doc;
    }

}
