package com.betarec.data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nd4j.linalg.ops.transforms.Transforms.cosineSim;


public class UserMovieVectorMap {
    public static final String USER_VECTOR_FILE = "./Data/vecs/AUTO_REC_MLP_USER_VECS.npy";
    public static final String MOVIE_VECTOR_FILE = "./Data/vecs/AUTO_REC_MLP_MOVIE_VECS.npy";
    private static final Logger logger = LoggerFactory.getLogger(UserMovieVectorMap.class);
    public final long userNum;
    public final long vectorLength;
    private final INDArray userArray;
    private final long movieNum;
    private final INDArray movieArray;

    public UserMovieVectorMap(String userVectorPath, String movieVectorPath){
        // 使用 ND4J 读取 .npy 文件
        userArray = Nd4j.createFromNpyFile(new File(userVectorPath));
        userNum = userArray.shape()[0];
        vectorLength = userArray.shape()[1];

        movieArray = Nd4j.createFromNpyFile(new File(movieVectorPath));
        movieNum = movieArray.shape()[0];
    }

    public INDArray getUserVec(long userId) {
        return userArray.get(NDArrayIndex.point(userId), NDArrayIndex.all());
    }

    public INDArray getMovieVec(long movieId) {
        return movieArray.get(NDArrayIndex.point(movieId), NDArrayIndex.all());
    }

    public Map<Integer, INDArray> getUserVecMap(List<Integer> userIds) {
        Map<Integer, INDArray> userVecMap = new HashMap<>(userIds.size());
        for (Integer userId : userIds) {
            userVecMap.put(userId, userArray.get(NDArrayIndex.point(userId), NDArrayIndex.all()));
        }
        return userVecMap;
    }

    public Map<Integer, INDArray> getMovieVecMap(List<Integer> movieIds) {
        Map<Integer, INDArray> movieVecMap = new HashMap<>(movieIds.size());
        for (Integer movieId : movieIds) {
            movieVecMap.put(movieId, movieArray.get(NDArrayIndex.point(movieId), NDArrayIndex.all()));
        }
        return movieVecMap;
    }

    public List<Integer> sortByTower(long userId, List<Integer> movieIds) {
        //ucf
        INDArray userVec = getUserVec(userId);
        Map<Integer, INDArray> movieVecMap = getMovieVecMap(movieIds);
        return movieIds.stream().sorted((a, b) -> {
            double acos = cosineSim(movieVecMap.get(a), userVec);
            double bcos = cosineSim(movieVecMap.get(b), userVec);
            return Double.compare(bcos, acos);
        }).toList();
    }

    public List<Integer> getMovieByIcf(int movieId, List<Integer> movieIds) {
        //icf
        INDArray movieVec = getMovieVec(movieId);
        Map<Integer, INDArray> movieVecMap = getMovieVecMap(movieIds);
        return movieIds.stream().sorted((a, b) -> {
            double acos = cosineSim(movieVecMap.get(a), movieVec);
            double bcos = cosineSim(movieVecMap.get(b), movieVec);
            return Double.compare(bcos, acos);
        }).toList();
    }
}
