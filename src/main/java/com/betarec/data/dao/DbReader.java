package com.betarec.data.dao;

import gen.data.pojo.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 相关数据的读操作
 */
public interface DbReader {
    int getMaxMovieId();

    int getMinMovieId();

    List<Integer> getMovieIds(@Param("beginMovieId") int beginMovieId, @Param("endMovieId") int endMovieId);

    int getMaxUserId();

    int getMinUserId();

    List<Integer> getUserIds(@Param("beginUserId") int beginUserId, @Param("endUserId") int endUserId);

    List<Rating> getUsersRatings(@Param("userIds") List<Integer> userIds);

    @MapKey("movieId")
    Map<Integer, Movie> getRangeMovies(@Param("beginMovieId") int beginMovieId, @Param("endMovieId") int endMovieId);

    @MapKey("movieId")
    Map<Integer, Movie> getMovies(@Param("movieIds") List<Integer> movieIds);

    @MapKey("movieId")
    Map<Integer, Link> getLinks(@Param("movieIds") List<Integer> movieIds);

    @MapKey("tagId")
    Map<Integer, GenomeTag> getGenomeTags(@Param("tagIds") List<Integer> genomeTagIds);

    List<GenomeScore> getGenomeScoresByMovieId(Integer movieId);

    List<GenomeScore> getGenomeScoresByTagId(Integer tagId);

    List<Rating> getRatingsByUserId(Integer userId);

    List<Rating> getRatingsByMovieId(Integer movieId);

    List<Tag> getTagsByUserId(Integer userId);

    List<Tag> getTagsByMovieId(Integer movieId);
}
