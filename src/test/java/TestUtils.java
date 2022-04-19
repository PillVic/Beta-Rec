import com.betarec.pojo.Movie;
import org.junit.Test;

import static com.betarec.utils.ObjectAnalyzer.toJsonString;

public class TestUtils {
    @Test
    public void testToString(){
        Movie movie = new Movie("1,movie,genres1|grenres2");
        System.out.println(toJsonString(movie));
    }
}
