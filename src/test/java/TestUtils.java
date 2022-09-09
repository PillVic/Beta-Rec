import com.betarec.pojo.Movie;
import org.junit.Test;

import static com.betarec.utils.ObjectAnalyzer.toJsonString;
import static com.betarec.utils.ObjectAnalyzer.toJsonStringV2;

public class TestUtils {
    @Test
    public void testToString(){
        Movie movie = new Movie("1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy");
        System.out.println(toJsonString(movie));
        System.out.println(toJsonStringV2(movie));
    }
}
