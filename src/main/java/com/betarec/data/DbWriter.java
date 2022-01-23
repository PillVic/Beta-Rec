package com.betarec.data;

import com.betarec.pojo.*;

public interface DbWriter {
    void insertMovie(Movie movie);
    void insertLink(Link link);
    void insertRating(Rating rating);
    void insertGenomeTag(GenomeTag genomeTag);
    void insertGenomeScore(GenomeScore genomeScore);
}
