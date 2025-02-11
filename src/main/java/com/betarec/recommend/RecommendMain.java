package com.betarec.recommend;

import com.betarec.data.Resource;
import com.betarec.data.UserMovieVectorMap;
import com.betarec.index.MovieWrapper;
import com.betarec.recommend.recall.DocRecall;
import com.betarec.recommend.sort.CoarseSort;
import com.betarec.utils.ArgMainBase;
import com.betarec.utils.Ticker;
import gen.service.ModelReq;
import gen.service.ModelResp;
import gen.service.ModelService;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.betarec.index.MovieWrapper.*;
import static com.betarec.recommend.sort.CoarseSort.modelRankTopN;
import static com.betarec.service.ModelServiceClientHelper.buildModelServiceClient;
import static com.betarec.utils.Flags.LOCAL_SERVER;

public class RecommendMain extends ArgMainBase {
    private static final Logger logger = LoggerFactory.getLogger(RecommendMain.class);

    @Option(name = "-movie_index_path", required = true, usage = "movie lucene index path")
    public String movieIndexPath;

    @Option(name = "-user_index_path", required = true, usage = "user lucene index path")
    public String userIndexPath;

    @Option(name = "-user_vec_path", required = true, usage = "user vector path")
    public String userVecPath;

    @Option(name = "-movie_vec_path", required = true, usage = "movie vector path")
    public String movieVecPath;

    @Option(name = "-model-service-port", required = true, usage = "model service port number")
    public int modelServicePort;

    @Option(name = "-rank-model", required = true, usage = "rank model name")
    public String rankModel;

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
            ticker.tick("load-movie-index");
            IndexSearcher userDocSearcher = Resource.getIndexSearcher(userIndexPath);
            ticker.tick("load-user-index");
            UserMovieVectorMap userMovieVectorMap = new UserMovieVectorMap(userVecPath, movieVecPath);
            ticker.tick("load-user-movie-npy");
            CoarseSort coarseSort = new CoarseSort(userMovieVectorMap);
            Resource r = new Resource();
            ticker.tick("init-resource");
            ModelService.Client modelServiceClient = buildModelServiceClient(LOCAL_SERVER, modelServicePort);
            ticker.tick("init-thrift-client");

            //recall:icf, ucf, basic(genres recall),
            Set<Integer> seenMovies = r.dbReader.getRatingsByUserId(userId).stream().map(t -> t.movieId).collect(Collectors.toSet());
            ticker.tick("get-seen-movies" + seenMovies.size());

            int recallLimit = limit * 100 + seenMovies.size();
            List<Document> movieDocs = new DocRecall(userDocSearcher, movieDocSearcher).movieRecallByUserGenres(401, recallLimit);
            ticker.tick("recall-" + recallType + recallLimit);

            //mask seen movie
            int beforeMaskedSize = movieDocs.size();
            movieDocs.removeIf(t -> seenMovies.contains(getMovieIdFromDoc(t)));
            ticker.tick("masked" + (movieDocs.size() - beforeMaskedSize));

            //coarse sort
            int coarseLimit = limit * 10;
            movieDocs = coarseSort.coarseTopN(userId, movieDocs, coarseLimit);
            ticker.tick("coarse-sort" + coarseLimit);

            //sort
            ModelReq req = new ModelReq(userId, movieDocs.stream().map(MovieWrapper::getMovieIdFromDoc).toList(), rankModel);
            ModelResp resp = modelServiceClient.movieModelRank(req);
            ticker.tick("thrift-rpc");
            movieDocs = modelRankTopN(movieDocs, limit, resp);
            ticker.tick("rank-top" + limit);
            logger.info("[movieModelRank]:{}", resp);

            for (Document document : movieDocs) {
                logger.info("movieId:{}, movie genres:{}", getMovieIdFromDoc(document), document.get(MOVIE_GENRES_ALL));
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
