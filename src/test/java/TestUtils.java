import com.betarec.pojo.Movie;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.betarec.utils.ObjectAnalyzer.toJsonString;
import static com.betarec.utils.ObjectAnalyzer.toJsonStringV2;

public class TestUtils {
    Logger logger = LoggerFactory.getLogger(TestUtils.class);
    @Test
    public void testToString(){
        Movie movie = new Movie("1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy");
        System.out.println(toJsonString(movie));
        System.out.println(toJsonStringV2(movie));
    }

    @Test
    public void testLog(){
        logger.info("hello info log");
        logger.error("hello error log");
    }
}
