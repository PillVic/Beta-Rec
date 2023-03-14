package com.betarec.data.service.impl;

import com.betarec.data.Resource;
import com.betarec.data.dao.DbReader;
import com.betarec.data.service.DbReaderService;
import gen.data.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DbReaderServiceImpl implements DbReaderService {
    @Autowired
    private final DbReader dbReader;
    public DbReaderServiceImpl(Resource r){
        this.dbReader = r.dbReader;
    }

    @Override
    public int getMaxMovieId() {
        return dbReader.getMaxMovieId();
    }

    @Override
    public int getMinMovieId() {
        return dbReader.getMinMovieId();
    }

    @Override
    public List<Integer> getMovieIds(int beginMovieId, int endMovieId) {
        return dbReader.getMovieIds(beginMovieId, endMovieId);
    }

    @Override
    public int getMaxUserId() {
        return dbReader.getMaxUserId();
    }

    @Override
    public int getMinUserId() {
        return dbReader.getMinUserId();
    }

    @Override
    public List<Integer> getUserIds(int beginUserId, int endUserId) {
        return dbReader.getUserIds(beginUserId, endUserId);
    }

    @Override
    public Map<Integer, Movie> getMovies(List<Integer> movieIds) {
        return dbReader.getMovies(movieIds);
    }

    @Override
    public Map<Integer, Link> getLinks(List<Integer> movieIds) {
        return dbReader.getLinks(movieIds);
    }

    @Override
    public Map<Integer, GenomeTag> getGenomeTags(List<Integer> genomeTagIds) {
        return dbReader.getGenomeTags(genomeTagIds);
    }

    @Override
    public List<GenomeScore> getGenomeScoresByMovieId(Integer movieId) {
        return dbReader.getGenomeScoresByMovieId(movieId);
    }

    @Override
    public List<GenomeScore> getGenomeScoresByTagId(Integer tagId) {
        return dbReader.getGenomeScoresByTagId(tagId);
    }

    @Override
    public List<Rating> getRatingsByUserId(Integer userId) {
        return dbReader.getRatingsByUserId(userId);
    }

    @Override
    public List<Rating> getRatingsByMovieId(Integer movieId) {
        return dbReader.getRatingsByMovieId(movieId);
    }

    @Override
    public List<Tag> getTagsByUserId(Integer userId) {
        return dbReader.getTagsByUserId(userId);
    }

    @Override
    public List<Tag> getTagsByMovieId(Integer movieId) {
        return dbReader.getTagsByMovieId(movieId);
    }
}
