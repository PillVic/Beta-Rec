package com.betarec.recommend;

import com.betarec.data.Resource;
import com.betarec.recommend.recall.DocRecall;
import com.betarec.utils.ArgMainBase;
import com.betarec.utils.Ticker;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.betarec.index.MovieWrapper.*;

public class RecommendMain extends ArgMainBase {
    private static final Logger logger = LoggerFactory.getLogger(RecommendMain.class);

    @Option(name = "-movie_index_path", required = true, usage = "movie lucene index path")
    public String movieIndexPath;

    @Option(name = "-user_index_path", required = true, usage = "user lucene index path")
    public String userIndexPath;

    @Option(name = "-userId", required = true, usage = "recommend usage")
    public int userId = -1;

    @Option(name = "-recall", required = false, usage = "recall movie strategy")
    public String recallType = "default";

    @Option(name = "-limit", required = false, usage = "recommend movie limit")
    public int limit = 30;

    @Override
    public void run() {
        try {

            Ticker ticker = new Ticker();

            IndexSearcher movieDocSearcher = Resource.getIndexSearcher(movieIndexPath);
            IndexSearcher userDocSearcher = Resource.getIndexSearcher(userIndexPath);
            Resource r = new Resource();
            ticker.tick("init");

            //recall:icf, ucf, basic(genres recall),
            Set<Integer> seenMovies = r.dbReader.getRatingsByUserId(userId).stream().map(t -> t.movieId).collect(Collectors.toSet());
            ticker.tick("get-seen-movies" + seenMovies.size());

            List<Document> movieDocs = new DocRecall(userDocSearcher, movieDocSearcher).movieRecallByUserGenres(401, limit + seenMovies.size());
            ticker.tick("recall-" + recallType + limit);

            //mask seen movie
            int beforeMaskedSize = movieDocs.size();
            movieDocs.removeIf(t -> seenMovies.contains(Integer.parseInt(t.get(MOVIE_ID))));
            ticker.tick("masked" + (movieDocs.size() - beforeMaskedSize));

            //coarse sort
            //fixme

            //sort
            //fixme

            for (Document document : movieDocs) {
                logger.info("movieId:{}, movie genres:{}", document.get(MOVIE_ID), document.get(MOVIE_GENRES_ALL));
            }
            logger.info("recommend userId:{}, recallType:{}, limit:{}, ticker:{}", userId, recallType, limit, ticker);
        } catch (Exception e) {
            logger.error("[RecommendMain ERROR]:userId:{}, recallType:{}, limit:{}", userId, recallType, limit, e);
        }
    }

    public static void main(String[] args) {
        new RecommendMain().parseArgsAndRun(args);
    }
}
