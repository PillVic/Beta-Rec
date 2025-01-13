package com.betarec.index.builder;

import com.betarec.data.Resource;
import com.betarec.index.UserWrapper;
import com.betarec.utils.ArgMainBase;
import com.betarec.utils.StatCount;
import com.betarec.utils.Ticker;
import gen.data.pojo.Movie;
import gen.data.pojo.Rating;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UserIndexBuilder extends ArgMainBase {
    @Option(name = "-userBatchSize", required = false, usage = "scan user batch")
    public int userBatchSize = 200;

    @Option(name = "-movieBatchSize", required = false, usage = "build movie batchSize")
    public int movieBatchSize = 200;

    @Option(name = "-index", required = false, usage = "movie index path")
    public static String USER_INDEX_DIR = "Data/index/user";

    @Option(name = "-threads", required = false, usage = "write index thread")
    public int threads = 20;

    private static final Logger logger = LoggerFactory.getLogger(MovieIndexBuilder.class);
    private Map<Integer, Movie> movieMap = Collections.emptyMap();
    private IndexWriter indexWriter;
    private MMapDirectory directory;
    private Resource r;
    private ExecutorService executor;

    @Override
    public void run() {
        try {
            r = new Resource();
            initIndexWriter();
            executor = Executors.newFixedThreadPool(threads);
            StatCount statCount = new StatCount();

            buildAllMovieMap(movieBatchSize);
            if (movieMap.isEmpty()) {
                logger.error("[build movie map fail]:empty map");
                return;
            }
            logger.info("build movie map, size:{}", movieMap.size());

            logger.info("start write user index");
            int minUserId = r.dbReader.getMinUserId();
            int maxUserId = r.dbReader.getMaxUserId();
            logger.info("minUserId:{}, maxUserId:{}", maxUserId, maxUserId);
            for (int i = minUserId; i <= maxUserId; i += userBatchSize) {
                final int beginUserId = i;
                final int endUserId = i + userBatchSize;
                executor.submit(() -> indexUserRange(beginUserId, endUserId, statCount));
            }

            executor.close();
            logger.info("write index done, statCount:{}", statCount);
            indexWriter.commit();
            indexWriter.close();
            directory.close();
        } catch (Exception e) {
            logger.error("[Build User Index ERROR]", e);
        }
    }

    private void buildAllMovieMap(int batchMovieSize) {
        try {
            int minMovieId = r.dbReader.getMinMovieId();
            int maxMovieId = r.dbReader.getMaxMovieId();
            logger.info("minMovieId:{}, maxMovieId:{}", minMovieId, maxMovieId);

            Map<Integer, Movie> movieMap = new HashMap<>(maxMovieId - minMovieId + 1);
            List<Future<Map<Integer, Movie>>> futures = new ArrayList<>();
            for (int i = minMovieId; i <= maxMovieId; i += batchMovieSize) {
                final int beginMovieId = i;
                final int endMovieId = i + batchMovieSize;
                var future = executor.submit(() -> r.dbReader.getRangeMovies(beginMovieId, endMovieId));
                futures.add(future);
            }
            for (var future : futures) {
                Map<Integer, Movie> subMovieMap = future.get();
                if (!subMovieMap.isEmpty()) {
                    movieMap.putAll(subMovieMap);
                }
            }

            this.movieMap = movieMap;
        } catch (Exception e) {
            logger.error("[getAllMovies ERROR], batchSize:{}", batchMovieSize, e);
        }
    }

    private void indexUserRange(int beginUserId, int endUserId, StatCount statCount) {
        try {
            Ticker ticker = new Ticker();
            List<Integer> userIds = r.dbReader.getUserIds(beginUserId, endUserId);
            List<Rating> ratings = r.dbReader.getUsersRatings(userIds);
            ticker.tick("db");

            if (CollectionUtils.isEmpty(ratings)) {
                statCount.count("empty-range");
            }

            Map<Integer, List<Integer>> userMoviesIdsMap = ratings.stream()
                    .collect(Collectors.toMap(k -> k.userId, v -> new ArrayList<>(List.of(v.movieId)),
                            (a, b) -> {
                                a.addAll(b);
                                return a;
                            }));
            for (Integer userId : userMoviesIdsMap.keySet()) {
                List<Movie> movies = userMoviesIdsMap.getOrDefault(userId, Collections.emptyList())
                        .stream().map(movieId -> movieMap.get(movieId)).toList();
                if (movies.size() == 1) {
                    statCount.count("single-movie-user");
                }
                UserWrapper userWrapper = new UserWrapper(userId).setSeenMovies(movies);
                indexWriter.addDocument(userWrapper.getDoc());
                statCount.count("user");
            }

            indexWriter.commit();
            ticker.tick("commit");
            logger.info("indexUserRange {},{}, ticker:{}", beginUserId, endUserId, ticker);
        } catch (Exception e) {
            logger.error("[indexMovieRange ERROR]: beginUserId:{}, endUserId:{}", beginUserId, endUserId, e);
        }
    }

    private void initIndexWriter() {
        try {
            directory = new MMapDirectory(Path.of(USER_INDEX_DIR));
            Analyzer analyzer = new KeywordAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(directory, config);
        } catch (Exception e) {
            logger.info("init index writer error", e);
        }
    }

    public static void main(String[] args) {
        new UserIndexBuilder().parseArgsAndRun(args);
    }
}
