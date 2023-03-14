import com.betarec.data.pojo.*;
import gen.data.pojo.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.betarec.data.DataBuilder.*;
import static com.betarec.data.pojo.PojoParser.*;
import static com.betarec.utils.Flags.COMMON_FILE_PATH;
import static com.betarec.utils.ParseFile.readLines;

public class TestParsePojo {
    private static final Logger logger = LoggerFactory.getLogger(TestParsePojo.class);

    @Test
    public void testParseGenomeScore(){
        List<String> lines = readLines(COMMON_FILE_PATH + GNOME_SCORE_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeScore genomeScore = parseGenomeScore(line);
            logger.info("genomeScore:{}", genomeScore);
        }

    }

    @Test
    public void testGenomeTag(){
        List<String> lines = readLines(COMMON_FILE_PATH + GENOME_TAG_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            GenomeTag genomeTag = parseGenomeTag(line);
            logger.info("genomeTag:{}", genomeTag);
        }
    }

    @Test
    public void testLink(){
        List<String> lines = readLines(COMMON_FILE_PATH + LINK_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Link link = parseLink(line);
            logger.info("link:{}", link);
        }
    }

    @Test
    public void testParseMovieContainYear(){
        String line = "7,Sabrina (1995),Comedy|Romance";
        Movie movie = parseMovie(line);
        logger.info("movie:{}", movie);
    }

    @Test
    public void testParseMovieWithoutYear(){
        String line = "162704,California Winter,Drama ";
        Movie movie = parseMovie(line);
        logger.info("movie:{}", movie);

        String line2 = "204770,1013 Briar Lane,Horror";
        Movie movie2 = parseMovie(line2);
        logger.info("movie2:{}", movie2);
    }

    @Test
    public void testParseMovieWithoutGenres(){
        String line = "83773,Away with Words (San tiao ren) (1999),(no genres listed)";
        Movie movie = parseMovie(line);
        logger.info("movie:{}", movie);
    }



    @Test
    public void testRating(){
        List<String> lines = readLines(COMMON_FILE_PATH + RATING_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Rating rating = parseRating(line);
            logger.info("rating:{}", rating);
        }

    }

    @Test
    public void testTag(){
        List<String> lines = readLines(COMMON_FILE_PATH + TAG_FILE, 20);
        for(int i=1;i<lines.size();i++){
            String line = lines.get(i);
            Tag tag = parseTag(line);
            logger.info("tag:{}", tag);
        }

    }

}
