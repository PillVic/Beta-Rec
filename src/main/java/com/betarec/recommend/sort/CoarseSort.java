package com.betarec.recommend.sort;


import com.betarec.data.UserMovieVectorMap;
import com.betarec.index.MovieWrapper;
import gen.service.ModelResp;
import org.apache.lucene.document.Document;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.betarec.index.MovieWrapper.getMovieIdFromDoc;
import static com.betarec.utils.FunctionUtils.topN;
import static org.nd4j.linalg.ops.transforms.Transforms.cosineSim;

public class CoarseSort {
    private final UserMovieVectorMap userMovieVectorMap;

    public CoarseSort(UserMovieVectorMap userMovieVectorMap) {
        this.userMovieVectorMap = userMovieVectorMap;
    }

    public List<Document> coarseTopN(long userId, List<Document> movieDocs, int limit) {
        List<Integer> movieIds = movieDocs.stream().map(MovieWrapper::getMovieIdFromDoc).toList();

        INDArray userVec = userMovieVectorMap.getUserVec(userId);
        Map<Integer, INDArray> movieVecMap = userMovieVectorMap.getMovieVecMap(movieIds);

        return topN(movieDocs, limit, (a, b) -> {
            int movieIdA = getMovieIdFromDoc(a);
            int movieIdB = getMovieIdFromDoc(b);

            INDArray aVec = movieVecMap.get(movieIdA);
            INDArray bVec = movieVecMap.get(movieIdB);

            double simA = cosineSim(aVec, userVec);
            double simB = cosineSim(bVec, userVec);

            return Double.compare(simB, simA);
        });
    }

    public static List<Document> modelRankTopN(List<Document> movieDocs, int limit, ModelResp resp) {
        Map<Integer, Double> movieScoreMap = resp.movieRanks.stream().collect(Collectors.toMap(k -> k.movieId, v -> v.score));
        return topN(movieDocs, limit, (a, b) -> {
            double scoreA = movieScoreMap.get(getMovieIdFromDoc(a));
            double scoreB = movieScoreMap.get(getMovieIdFromDoc(b));
            return Double.compare(scoreB, scoreA);
        });
    }
}
