package com.betarec.recommend.sort;


import com.betarec.data.UserMovieVectorMap;
import com.betarec.index.MovieWrapper;
import org.apache.lucene.document.Document;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.betarec.index.MovieWrapper.getMovieIdFromDoc;
import static org.nd4j.linalg.ops.transforms.Transforms.cosineSim;

public class CoarseSort {
    private UserMovieVectorMap userMovieVectorMap;
    public CoarseSort(UserMovieVectorMap userMovieVectorMap){
        this.userMovieVectorMap = userMovieVectorMap;
    }
    public List<Document> coarseTopN(long userId, List<Document> movieDocs, int limit) {
        List<Integer> movieIds = movieDocs.stream().map(MovieWrapper::getMovieIdFromDoc).toList();

        INDArray userVec = userMovieVectorMap.getUserVec(userId);
        Map<Integer, INDArray> movieVecMap = userMovieVectorMap.getMovieVecMap(movieIds);

        movieDocs = movieDocs.stream().sorted((a, b) -> {
            int movieIdA = getMovieIdFromDoc(a);
            int movieIdB = getMovieIdFromDoc(b);

            INDArray aVec = movieVecMap.get(movieIdA);
            INDArray bVec = movieVecMap.get(movieIdB);

            double simA = cosineSim(aVec, userVec);
            double simB = cosineSim(bVec, userVec);

            return Double.compare(simB, simA);
        }).limit(limit).collect(Collectors.toList());
        return movieDocs;
    }
}
