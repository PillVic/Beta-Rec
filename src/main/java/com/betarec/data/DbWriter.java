package com.betarec.data;

import com.betarec.pojo.*;
import com.betarec.utils.Flags.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 相关数据的写入操作
 * 由于genome_scores, ratings的数据量过大，进行写入
* */
public interface DbWriter {
    void insertMovie(Movie movie);

    void insertLink(Link link);

    void insertRating(Rating rating);

    void insertGenomeTag(GenomeTag genomeTag);

    void insertGenomeScore(GenomeScore genomeScore);

    void insertRatings(@Param("ratings") List<Rating> ratings,
                       @Param("suffix") int suffix);

    void insertGenomeScores(@Param("scores") List<GenomeScore> scores,
                            @Param("suffix") int suffix);
}
