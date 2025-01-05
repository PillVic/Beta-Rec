package com.betarec.data;

import com.betarec.data.pojo.*;
import com.betarec.utils.ArgMainBase;
import com.betarec.utils.ParseFile;
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
    private static String mode;


    public static final String MOVIE_FILE = "movies.csv";
    public static final String LINK_FILE = "links.csv";
    public static final String TAG_FILE = "tags.csv";
    public static final String GENOME_TAG_FILE = "genome-tags.csv";
    public static final String GNOME_SCORE_FILE = "genome-scores.csv";
    public static final String RATING_FILE = "ratings.csv";

    public static void buildDb(ThreadPoolExecutor pool) {
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

    private static void buildMovieDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.movies");
        ParseFile.batchParse(COMMON_FILE_PATH + MOVIE_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<Movie> movies = lines.stream().map(Movie::new).collect(Collectors.toList());
                dbWriter.insertMovies(movies);
            }, lst);
        }, pool);
    }

    private static void buildLinkDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.links");
        ParseFile.batchParse(COMMON_FILE_PATH + LINK_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<Link> links = lines.stream().map(Link::new).collect(Collectors.toList());
                dbWriter.insertLinks(links);
            }, lst);
        }, pool);
    }

    private static void buildTagDb(ThreadPoolExecutor pool){
        logger.info("[BUILD DB]:MovieLens.tags");
        ParseFile.batchParse(COMMON_FILE_PATH + TAG_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<Tag> tags = lines.stream().map(Tag::new).collect(Collectors.toList());
                dbWriter.insertTags(tags);
            }, lst);
        }, pool);
    }

    private static void buildGenomeTagDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.genome_tags");
        ParseFile.batchParse(COMMON_FILE_PATH + GENOME_TAG_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<GenomeTag> genomeTags = lines.stream().map(GenomeTag::new).collect(Collectors.toList());
                dbWriter.insertGenomeTags(genomeTags);
            }, lst);
        }, pool);
    }

    private static void buildGenomeScoreDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.genome_scores");
        ParseFile.batchParse(COMMON_FILE_PATH + GNOME_SCORE_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<GenomeScore> genomeScores = lst.stream().map(GenomeScore::new).toList();
                dbWriter.insertGenomeScores(genomeScores);
            }, lst);
        }, pool);
    }

    private static void buildRatingsDb(ThreadPoolExecutor pool) {
        logger.info("[BUILD DB]:MovieLens.ratings");
        ParseFile.batchParse(COMMON_FILE_PATH + RATING_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<Rating> ratings = lines.stream().map(Rating::new).toList();
                dbWriter.insertRatings(ratings);
            }, lst);
        }, pool);
    }

    @Override
    public void run() {
        logger.info("mode:{}", mode);
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildDb(pool);
        pool.shutdown();
    }

    public static void main(String[] args) {
        new DataBuilder().parseArgsAndRun(args);
    }
}
