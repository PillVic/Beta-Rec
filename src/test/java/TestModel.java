import com.betarec.data.UserMovieVectorMap;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.betarec.data.UserMovieVectorMap.MOVIE_VECTOR_FILE;
import static com.betarec.data.UserMovieVectorMap.USER_VECTOR_FILE;
import static org.nd4j.linalg.ops.transforms.Transforms.cosineSim;

public class TestModel {
    Logger logger = LoggerFactory.getLogger((TestModel.class));
    private final UserMovieVectorMap userMovieVectorMap = new UserMovieVectorMap(USER_VECTOR_FILE, MOVIE_VECTOR_FILE);

    @Test
    public void testGetUserVecs() {
        List<Integer> userIds = List.of(1, 2, 3);
        Map<Integer, INDArray> userVecMap = userMovieVectorMap.getUserVecMap(userIds);
        for (Integer userId : userIds) {
            logger.info("userId:{}, vecs:{}", userId, userVecMap.get(userId));
        }
    }

    @Test
    public void testGetMovieVecs() {
        List<Integer> movieIds = List.of(1, 2, 3);
        Map<Integer, INDArray> movieVecMap = userMovieVectorMap.getMovieVecMap(movieIds);
        for (Integer movieId : movieIds) {
            logger.info("movieId:{}, vecs:{}", movieId, movieVecMap.get(movieId));
        }
    }

    @Test
    public void testCosine() {
        List<Integer> ids = List.of(5, 6, 7);
        Map<Integer, INDArray> userVecMap = userMovieVectorMap.getUserVecMap(ids);

        Map<Integer, INDArray> movieVecMap = userMovieVectorMap.getMovieVecMap(ids);

        for (Integer id : ids) {
            INDArray movieVec = movieVecMap.get(id);
            INDArray userVec = userVecMap.get(id);
            logger.info("id:{}, cos-sim:{}", id, cosineSim(movieVec, userVec));
        }
    }

    @Test
    public void testSort() {
        List<Double> lst = List.of(3.9, 3.2, 3.3, 2.4);
        List<Double> lst2 = lst.stream().sorted((Double a, Double b) -> {
            return Double.compare(b, a);
        }).toList();
        logger.info("lst2:{}", lst2);
    }
}
