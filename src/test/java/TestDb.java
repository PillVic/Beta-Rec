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
        dbWriter.insertRatings(ratings, 0);

        List<GenomeScore> scores = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GenomeScore score = new GenomeScore(
                    String.format("%d,%d,%f",
                            i, i * 2, 8 * 1.0 / 2));
            scores.add(score);
        }
        dbWriter.insertGenomeScores(scores, 0);
    }
}
