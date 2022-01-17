package com.betarec.data;

import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.GenomeTag;
import com.betarec.pojo.Link;
import com.betarec.pojo.Movie;

public interface DbWriter {
    void insertMovie(Movie movie);
    void insertLink(Link link);
    void insertGenomeTag(GenomeTag genomeTag);
    void insertGenomeScore(GenomeScore genomeScore);
}
