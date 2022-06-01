import com.betarec.data.DbReader;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.Link;
import com.betarec.pojo.Movie;
import com.betarec.pojo.Rating;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDb {
    private static final Logger logger = LoggerFactory.getLogger(TestDb.class);
    DbReader dbReader = Resource.getResource().dbReader;

    @Test
    public void testInsertRatings() {
        List<Rating> ratings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Rating rating = new Rating(
                    String.format("%d,%d,%f,%d",
                            i, i * 2, i * 1.0 / 10, System.currentTimeMillis()));
            ratings.add(rating);
        }
        DbWriter dbWriter = Resource.getResource().dbWriter;
        dbWriter.insertRatings(ratings);

        List<GenomeScore> scores = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GenomeScore score = new GenomeScore(
                    String.format("%d,%d,%f",
                            i, i * 2, 8 * 1.0 / 2));
            scores.add(score);
        }
        dbWriter.insertGenomeScores(scores);
    }

    @Test
    public void testDbReader() {
        List<Integer> movieIds = List.of(1, 2, 3, 4, 5);
        for (var entry : dbReader.getMovies(movieIds).entrySet()) {
            System.out.println(entry);
        }
        List<Integer> tagIds = List.of(1, 2, 3);
        for (var entry : dbReader.getGenomeTags(movieIds).entrySet()) {
            System.out.println(entry);
        }
        for (var entry : dbReader.getLinks(movieIds).entrySet()) {
            System.out.println(entry);
        }
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
        for(var movieId: movieIds){
            Link link = linkMap.get(movieId);
            logger.info("movieId:{}, link:{}", movieId, link);
        }
    }

}
