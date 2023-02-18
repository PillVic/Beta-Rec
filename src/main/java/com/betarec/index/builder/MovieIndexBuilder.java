package com.betarec.index.builder;

import com.betarec.data.dao.DbReader;
import com.betarec.data.Resource;
import com.betarec.index.MovieWrapper;
import com.betarec.data.pojo.GenomeScore;
import com.betarec.data.pojo.Movie;
import com.betarec.data.pojo.Rating;
import com.betarec.utils.StatCount;
import com.betarec.utils.Ticker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MovieIndexBuilder implements Runnable {
    public int batchSize = 200;
    public int threads = 20;

    public static String MOVIE_INDEX_DIR = "Data/index/movieIndex";
    private static final Logger logger = LoggerFactory.getLogger(MovieIndexBuilder.class);
    private IndexWriter indexWriter;
    private MMapDirectory directory;
    private Resource r;

    @Override
    public void run() {
        try {
            r = Resource.getResource();
            initIndexWriter();
            DbReader dbReader = Resource.getResource().dbReader;
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            StatCount statCount = new StatCount();

            int minMovieId = dbReader.getMinMovieId();
            int maxMovieId = dbReader.getMaxMovieId();
            for (int i = minMovieId; i <= maxMovieId; i += batchSize) {
                final int beginMovieId = i;
                final int endMovieId = i + batchSize;
                executor.submit(() -> {
                    indexMovieRange(beginMovieId, endMovieId, statCount);
                });
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            logger.info("write index done, statCount:{}", statCount);
            indexWriter.commit();
            indexWriter.close();
            directory.close();
        } catch (Exception e) {
            logger.error("movie Index build ERROR:", e);
        }
    }

    private void initIndexWriter() {
        try {
            directory = new MMapDirectory(Path.of(MOVIE_INDEX_DIR));
            Analyzer analyzer = new KeywordAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(directory, config);
        } catch (Exception e) {
            logger.info("init index writer error", e);
        }
    }

    private void indexMovieRange(int beginMovieId, int endMovieId, StatCount statCount) {
        try {
            Ticker ticker = new Ticker();
            List<Integer> movieIds = r.dbReader.getMovieIds(beginMovieId, endMovieId);
            if(CollectionUtils.isEmpty(movieIds)){
                return;
            }
            Map<Integer, Movie> movieMap = r.dbReader.getMovies(movieIds);
            Map<Integer, List<GenomeScore>> genomeScoresMap = new HashMap<>(movieIds.size());
            Map<Integer, List<Rating>> ratingsMap = new HashMap<>(movieIds.size());
            movieIds.forEach(movieId -> {
                genomeScoresMap.put(movieId, r.dbReader.getGenomeScoresByMovieId(movieId));
                ratingsMap.put(movieId, r.dbReader.getRatingsByMovieId(movieId));
            });
            ticker.tick("db");
            for (Movie movie : movieMap.values()) {
                statCount.count("movie");
                List<GenomeScore> genomeScores = genomeScoresMap.getOrDefault(movie.movieId, Collections.emptyList());
                if (CollectionUtils.isEmpty(genomeScores)) {
                    statCount.count("empty-genome-score");
                }
                List<Rating> ratings = ratingsMap.getOrDefault(movie.movieId, Collections.emptyList());
                if (CollectionUtils.isEmpty(ratings)) {
                    statCount.count("empty-rating");
                }
                MovieWrapper wrapper = new MovieWrapper(movie).setRatings(ratings).setGenomeScores(genomeScores);
                indexWriter.addDocument(wrapper.getDoc());
            }
            ticker.tick("add-doc");
            indexWriter.commit();
            ticker.tick("commit");
            logger.info("indexMovieRange {},{}, ticker:{}", beginMovieId, endMovieId, ticker);
        } catch (Exception e) {
            logger.error("indexMovieRange ERROR {}, {}", beginMovieId, endMovieId, e);
        }
    }

    public static void main(String[] args) {
        MovieIndexBuilder builder = new MovieIndexBuilder();
        builder.run();
    }
}
