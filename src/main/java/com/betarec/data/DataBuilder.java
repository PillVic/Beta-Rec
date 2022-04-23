package com.betarec.data;

import com.betarec.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @desc 导入数据库，以及构建倒排索引由这个类统一构建
* */
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

        logger.info("[BUILD DB]:MovieLens.genome_scores");
        GenomeScore.buildGenomeScoreDb();
        logger.info("[BUILD DB]:MovieLens.ratings");
        Rating.buildRatingsDb();
    }

    public static void main(String[] args) {
        buildDb();
    }
}
