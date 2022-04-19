import com.betarec.data.DbReader;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.Rating;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDb {
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
        DbReader dbReader = Resource.getResource().dbReader;
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
}
