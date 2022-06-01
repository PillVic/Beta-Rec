package com.betarec.data;

import com.betarec.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @desc 导入数据库，以及构建倒排索引由这个类统一构建
 */
public class DataBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DataBuilder.class);

    public static void buildDb(ThreadPoolExecutor pool) {
        //build MovieLens Db
        logger.info("[BUILD DB]:MovieLens.movies");
        Movie.buildMovieDb(pool);
        logger.info("[BUILD DB]:MovieLens.links");
        Link.buildLinkDb(pool);
        logger.info("[BUILD DB]:MovieLens.tags");
        Tag.buildTagDb(pool);
        logger.info("[BUILD DB]:MovieLens.genome_tags");
        GenomeTag.buildGenomeTagDb(pool);

        logger.info("[BUILD DB]:MovieLens.genome_scores");
        GenomeScore.buildGenomeScoreDb(pool);
        logger.info("[BUILD DB]:MovieLens.ratings");
        Rating.buildRatingsDb(pool);
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildDb(pool);
        pool.shutdown();
    }
}
