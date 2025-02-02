package com.betarec.data;

import com.betarec.data.pojo.PojoParser;
import com.betarec.utils.ArgMainBase;
import com.betarec.utils.ParseFile;
import gen.data.pojo.*;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

/**
 * @desc 导入数据库，以及构建倒排索引由这个类统一构建
 */
public class DataBuilder extends ArgMainBase {
    private static final Logger logger = LoggerFactory.getLogger(DataBuilder.class);


    @Option(name="-mode", required=true, usage = "write which")
    private String mode = "none";


    public static final String MOVIE_FILE = "movies.csv";
    public static final String LINK_FILE = "links.csv";
    public static final String TAG_FILE = "tags.csv";
    public static final String GENOME_TAG_FILE = "genome-tags.csv";
    public static final String GNOME_SCORE_FILE = "genome-scores.csv";
    public static final String RATING_FILE = "ratings.csv";

    private Resource r;

    public void buildDb(ThreadPoolExecutor pool) {
        Resource r = new Resource();
        if(mode.equals("all") || mode.equals("movie")){
            buildMovieDb(pool);
        }
        if(mode.equals("all") || mode.equals("link")){
            buildLinkDb(pool);
        }
        if(mode.equals("all") || mode.equals("tag")){
            buildTagDb(pool);
        }
        if(mode.equals("all") || mode.equals("genomeTag")){
            buildGenomeTagDb(pool);
        }
        if(mode.equals("all") || mode.equals("genomeScore")){
            buildGenomeScoreDb(pool);
        }
        if (mode.equals("all") || mode.equals("rating")) {
            buildRatingsDb(pool);
        }

    }

    private void buildMovieDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.movies");
        ParseFile.batchParse(COMMON_FILE_PATH + MOVIE_FILE, lst -> {
            new Resource().batchInsert((dbWriter, lines) -> {
                List<Movie> movies = lines.stream().map(PojoParser::parseMovie).collect(Collectors.toList());
                dbWriter.insertMovies(movies);
            }, lst);
        }, pool);
    }

    private void buildLinkDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.links");
        ParseFile.batchParse(COMMON_FILE_PATH + LINK_FILE, lst -> {
            new Resource().batchInsert((dbWriter, lines) -> {
                List<Link> links = lines.stream().map(PojoParser::parseLink).collect(Collectors.toList());
                dbWriter.insertLinks(links);
            }, lst);
        }, pool);
    }

    private void buildTagDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.tags");
        ParseFile.batchParse(COMMON_FILE_PATH + TAG_FILE, lst -> {
            r.batchInsert((dbWriter, lines) -> {
                List<Tag> tags = lines.stream().map(PojoParser::parseTag).collect(Collectors.toList());
                dbWriter.insertTags(tags);
            }, lst);
        }, pool);
    }

    private void buildGenomeTagDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.genome_tags");
        ParseFile.batchParse(COMMON_FILE_PATH + GENOME_TAG_FILE, lst -> {
            r.batchInsert((dbWriter, lines) -> {
                List<GenomeTag> genomeTags = lines.stream().map(PojoParser::parseGenomeTag).collect(Collectors.toList());
                dbWriter.insertGenomeTags(genomeTags);
            }, lst);
        }, pool);
    }

    private void buildGenomeScoreDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.genome_scores");
        ParseFile.batchParse(COMMON_FILE_PATH + GNOME_SCORE_FILE, lst -> {
            r.batchInsert((dbWriter, lines) -> {
                List<GenomeScore> genomeScores = lst.stream().map(PojoParser::parseGenomeScore).toList();
                dbWriter.insertGenomeScores(genomeScores);
            }, lst);
        }, pool);
    }

    private void buildRatingsDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.ratings");
        ParseFile.batchParse(COMMON_FILE_PATH + RATING_FILE, lst -> {
            r.batchInsert((dbWriter, lines) -> {
                List<Rating> ratings = lines.stream().map(PojoParser::parseRating).toList();
                dbWriter.insertRatings(ratings);
            }, lst);
        }, pool);
    }

    @Override
    public void run() {
        logger.info("mode:{}", mode);
        r =  new Resource();
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildDb(pool);
        pool.shutdown();
    }

    public static void main(String[] args) {
        new DataBuilder().parseArgsAndRun(args);
    }
}
