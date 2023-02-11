package com.betarec.index.builder;

import com.betarec.data.DbReader;
import com.betarec.data.Resource;
import com.betarec.index.MovieWrapper;
import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.Movie;
import com.betarec.pojo.Rating;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class MovieIndexBuilder implements Runnable {
    public int batchSize = 20;

    public static String MOVIE_INDEX_DIR = "Data/index/movieIndex";
    private IndexWriter writer;
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

            int minMovieId = 0;//dbReader.getMinMovieId();
            int maxMovieId = 20;//dbReader.getMaxMovieId();
            for (int i = minMovieId; i <= maxMovieId; i += batchSize) {
                indexMovieRange(i, i + batchSize);
            }

            logger.info("write index done");
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

    private void indexMovieRange(int beginMovieId, int endMovieId) {
        try {
            logger.info("indexMovieRange:{}, {}", beginMovieId, endMovieId);
            List<Integer> movieIds = r.dbReader.getMovieIds(beginMovieId, endMovieId);
            Map<Integer, Movie> movieMap = r.dbReader.getMovies(movieIds);
            for (Movie movie : movieMap.values()) {
                List<GenomeScore> genomeScores = r.dbReader.getGenomeScoresByMovieId(movie.movieId);
                List<Rating> ratings = r.dbReader.getRatingsByMovieId(movie.movieId);
                logger.info("movieId:{}, genomeScores:{}, ratings:{}",
                        movie.movieId, genomeScores.size(), ratings.size());
                MovieWrapper wrapper = new MovieWrapper(movie).setRatings(ratings).setGenomeScores(genomeScores);
                indexWriter.addDocument(wrapper.getDoc());
            }
        } catch (Exception e) {
            logger.error("indexMovieRange ERROR {}, {}", beginMovieId, endMovieId, e);
        }
    }

    public static void main(String[] args) {
        MovieIndexBuilder builder = new MovieIndexBuilder();
        builder.run();
    }
}
