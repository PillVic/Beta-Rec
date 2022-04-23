package com.betarec.data;

import com.betarec.pojo.GenomeTag;
import com.betarec.pojo.Link;
import com.betarec.pojo.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DataBuilder.class);
    public static void buildDb(){
        //build MovieLens Db
        logger.info("[BUILD DB]:MovieLens.movie");
        Movie.buildMovieDb();
        logger.info("[BUILD DB]:MovieLens.link");
        Link.buildLinkDb();
        logger.info("[BUILD DB]:MovieLens.genome_tags");
        GenomeTag.buildGenomeTagDb();
    }

    public static void main(String[] args) {
        buildDb();
    }
}
