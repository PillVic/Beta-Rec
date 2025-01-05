import com.betarec.data.Resource;
import com.betarec.data.dao.DbReader;
import com.betarec.data.pojo.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class TestDb {
    private static final Logger logger = LoggerFactory.getLogger(TestDb.class);
    DbReader dbReader = Resource.getResource().dbReader;


    @Test
    public void testGetMaxMovieId() {
        logger.info("maxMovieId:{}", dbReader.getMaxMovieId());
    }

    @Test
    public void testGetMinMovieId() {
        logger.info("minMovieId:{}", dbReader.getMinMovieId());
    }

    @Test
    public void testGetMovieIds() {
        int beginMovieId = 1;
        int endMovieId = 10;
        logger.info("movieIds, beginMovieId:{}, endMovieId:{}, movieIds:{}",
                beginMovieId, endMovieId, dbReader.getMovieIds(beginMovieId, endMovieId));
    }

    @Test
    public void testMaxUserId() {
        logger.info("MaxUserId:{}", dbReader.getMaxUserId());
    }

    @Test
    public void testMinUserId() {
        logger.info("MinUserId:{}", dbReader.getMinUserId());
    }

    @Test
    public void testGetUserIds() {
        int beginUserId = 1;
        int endUserId = 10;
        logger.info("beginUserId:{}, endUserId:{}, userIds:{}",
                beginUserId, endUserId, dbReader.getUserIds(beginUserId, endUserId));
    }

    @Test
    public void testGetMovies() {
        List<Integer> movieIds = List.of(1, 2, 3, 4, 5);
        Map<Integer, Movie> movieId2Movie = dbReader.getMovies(movieIds);
        for (var movieId : movieIds) {
            Movie movie = movieId2Movie.get(movieId);
            logger.info("movieId:{}, ,movie:{}", movieId, movie);
        }
    }

    @Test
    public void testGetLinks() {
        List<Integer> movieIds = List.of(1, 2, 3, 4, 5);
        Map<Integer, Link> linkMap = dbReader.getLinks(movieIds);
        for (var movieId : movieIds) {
            Link link = linkMap.get(movieId);
            logger.info("movieId:{}, link:{}", movieId, link);
        }
    }

    @Test
    public void testGetGenomeTags() {
        List<Integer> tagIds = List.of(1, 2, 3, 4, 5);
        Map<Integer, GenomeTag> genomeTagMap = dbReader.getGenomeTags(tagIds);
        for (var tagId : tagIds) {
            GenomeTag genomeTag = genomeTagMap.get(tagId);
            logger.info("tagId:{},  genomeTag:{}", tagId, genomeTag);
        }
    }

    @Test
    public void testGetGenomeScoresByMovieId() {
        int movieId = 1;
        List<GenomeScore> genomeScores = dbReader.getGenomeScoresByMovieId(movieId);
        for (GenomeScore genomeScore : genomeScores) {
            logger.info("genomeScore:{}", genomeScore);
        }
    }

    @Test
    public void testGetGenomeScoresByTagId() {
        int tagId = 1;
        List<GenomeScore> genomeScores = dbReader.getGenomeScoresByTagId(tagId);
        for (GenomeScore genomeScore : genomeScores) {
            logger.info("genomeScore:{}", genomeScore);
        }
    }

    @Test
    public void testGetRatingsByUserId() {
        int userId = 7;
        List<Rating> ratings = dbReader.getRatingsByUserId(userId);
        for (Rating rating : ratings) {
            logger.info("rating:{}", rating);
        }
    }


    @Test
    public void testGetRatingsByMovieId() {
        int movieId = 7;
        List<Rating> ratings = dbReader.getRatingsByMovieId(movieId);
        for (Rating rating : ratings) {
            logger.info("rating:{}", rating);
        }
    }

    @Test
    public void testGetTagsByUserId() {
        int userId = 14;
        List<Tag> tags = dbReader.getTagsByUserId(userId);
        for (Tag tag : tags) {
            logger.info("tag:{}", tag);
        }
    }

    @Test
    public void testGetTagsByMovieId() {
        int movieId = 110;
        List<Tag> tags = dbReader.getTagsByMovieId(movieId);
        for (Tag tag : tags) {
            logger.info("tag:{}", tag);
        }
    }
}
