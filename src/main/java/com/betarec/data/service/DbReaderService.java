package com.betarec.data.service;

import com.betarec.data.pojo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DbReaderService {
    int getMaxMovieId();

    int getMinMovieId();

    List<Integer> getMovieIds(int beginMovieId, @Param("endMovieId") int endMovieId);

    int getMaxUserId();

    int getMinUserId();

    List<Integer> getUserIds(int beginUserId, int endUserId);

    Map<Integer, Movie> getMovies(List<Integer> movieIds);

    Map<Integer, Link> getLinks(List<Integer> movieIds);

    Map<Integer, GenomeTag> getGenomeTags(List<Integer> genomeTagIds);

    List<GenomeScore> getGenomeScoresByMovieId(Integer movieId);

    List<GenomeScore> getGenomeScoresByTagId(Integer tagId);

    List<Rating> getRatingsByUserId(Integer userId);

    List<Rating> getRatingsByMovieId(Integer movieId);

    List<Tag> getTagsByUserId(Integer userId);

    List<Tag> getTagsByMovieId(Integer movieId);
}
