import com.betarec.data.pojo.Movie;
import com.betarec.utils.Ticker;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testTicker() throws InterruptedException {
        Ticker ticker = new Ticker();
        Map<String, Integer> events = Map.of(
                "A", 10,
                "B", 20,
                "C", 30
        );
        for(Map.Entry<String, Integer> entry: events.entrySet()){
            TimeUnit.MILLISECONDS.sleep(entry.getValue());
            ticker.tick(entry.getKey());
        }
        logger.info("ticker:{}", ticker);
    }
}
