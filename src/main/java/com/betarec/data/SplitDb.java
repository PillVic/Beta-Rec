package com.betarec.data;

import com.betarec.pojo.GenomeScore;
import com.betarec.pojo.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.betarec.utils.Flags.*;

/**
 * 所有涉及分表的读写都通过这个类来完成
 */
public class SplitDb {
    private static final Logger logger = LoggerFactory.getLogger(SplitDb.class);

    public static void insertRatings(List<Rating> ratings, DbWriter dbWriter) {
        try {
            Map<Integer, List<Rating>> partitionRatings = new HashMap<>(ratings.size());
            for (var rating : ratings) {
                int suffix = rating.movieId % RATING_SPLIT_TABLE_NUM;
                partitionRatings.computeIfAbsent(suffix, t -> new ArrayList<>());
                List<Rating> partitions = partitionRatings.get(suffix);
                partitions.add(rating);
            }
            for (var suffix : partitionRatings.keySet()) {
                dbWriter.insertRatings(partitionRatings.get(suffix), suffix);
            }
        } catch (Exception e) {
            logger.error("insertRatings ERROR:", e);
        }
    }

    public static void insertGenomeScores(List<GenomeScore> scores, DbWriter dbWriter) {
        try {
            Map<Integer, List<GenomeScore>> partitionScores = new HashMap<>(scores.size());
            for (var score : scores) {
                int suffix = score.movieId % SCORE_SPLIT_TABLE_NUM;
                partitionScores.computeIfAbsent(suffix, t -> new ArrayList<>());
                List<GenomeScore> partitions = partitionScores.get(suffix);
                partitions.add(score);
            }
            for (var suffix : partitionScores.keySet()) {
                dbWriter.insertGenomeScores(partitionScores.get(suffix), suffix);
            }
        }catch (Exception e){
            logger.error("insertGenomeScores ERROR:", e);
        }
    }
}
