package com.betarec.data;

import com.betarec.pojo.Movie;

public interface DbWriter {
    void insertMovie(Movie movie);
}
