import com.betarec.data.pojo.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.betarec.data.DataBuilder.*;
import static com.betarec.data.pojo.GenomeTag.GENOME_TAG_FILE;
import static com.betarec.utils.Flags.COMMON_FILE_PATH;
import static com.betarec.utils.ParseFile.readLines;

public class TestParsePojo {
    private static final Logger logger = LoggerFactory.getLogger(TestParsePojo.class);

    @Test
    public void testParseGenomeScore(){
        List<String> lines = readLines(COMMON_FILE_PATH + GNOME_SCORE_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeScore genomeScore = new GenomeScore(line);
            logger.info("genomeScore:{}", genomeScore);
        }

    }

    @Test
    public void testGenomeTag(){
        List<String> lines = readLines(COMMON_FILE_PATH + GENOME_TAG_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeTag genomeTag = new GenomeTag(line);
            logger.info("genomeTag:{}", genomeTag);
        }
    }

    @Test
    public void testLink(){
        List<String> lines = readLines(COMMON_FILE_PATH + LINK_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Link link = new Link(line);
            logger.info("link:{}", link);
        }
    }

    @Test
    public void testParseMovieContainYear(){
        String line = "7,Sabrina (1995),Comedy|Romance";
        Movie movie = new Movie(line);
        logger.info("movie:{}", movie);
    }

    @Test
    public void testParseMovieWithoutYear(){
        String line = "162704,California Winter,Drama ";
        Movie movie = new Movie(line);
        logger.info("movie:{}", movie);

        String line2 = "204770,1013 Briar Lane,Horror";
        Movie movie2 = new Movie(line2);
        logger.info("movie2:{}", movie2);
    }

    @Test
    public void testParseMovieWithoutGenres(){
        String line = "83773,Away with Words (San tiao ren) (1999),(no genres listed)";
        Movie movie = new Movie(line);
        logger.info("movie:{}", movie);
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
