import com.betarec.data.Resource;
import com.betarec.data.pojo.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;
import static com.betarec.utils.ParseFile.readLines;

public class TestParsePojo {
    private static final Logger logger = LoggerFactory.getLogger(TestParsePojo.class);

    @Test
    public void testParseGenomeScore(){
        List<String> lines = readLines(COMMON_FILE_PATH + GenomeScore.GNOME_SCORE_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeScore genomeScore = new GenomeScore(line);
            logger.info("genomeScore:{}", genomeScore);
        }

    }

    @Test
    public void testGenomeTag(){
        List<String> lines = readLines(COMMON_FILE_PATH + GenomeTag.GENOME_TAG_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeTag genomeTag = new GenomeTag(line);
            logger.info("genomeTag:{}", genomeTag);
        }
    }

    @Test
    public void testLink(){
        List<String> lines = readLines(COMMON_FILE_PATH + Link.LINK_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Link link = new Link(line);
            logger.info("link:{}", link);
        }
    }

    @Test
    public void testParseMovie(){
        List<String> lines = readLines(COMMON_FILE_PATH + Movie.MOVIE_FILE, 20);
        List<Movie> movies = lines.subList(1, lines.size()).stream().map(Movie::new).toList();
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Movie movie = new Movie(line);
            logger.info("movie:{}", movie);
        }
        Objects.requireNonNull(Resource.getResource().dbWriter).insertMovies(movies);
    }

    @Test
    public void testRating(){
        List<String> lines = readLines(COMMON_FILE_PATH + Rating.RATING_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Rating rating = new Rating(line);
            logger.info("rating:{}", rating);
        }

    }

    @Test
    public void testTag(){
        List<String> lines = readLines(COMMON_FILE_PATH + Tag.TAG_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Tag tag = new Tag(line);
            logger.info("tag:{}", tag);
        }

    }

}
