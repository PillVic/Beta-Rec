package com.betarec.data;

import com.betarec.pojo.*;
import com.betarec.utils.Flags.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 相关数据的写入操作
 * 由于genome_scores, ratings的数据量过大，进行写入
 */
public interface DbWriter {

    void insertRatings(@Param("ratings") List<Rating> ratings);

    void insertGenomeScores(@Param("scores") List<GenomeScore> scores);

    void insertGenomeTags(@Param("genomeTags") List<GenomeTag> genomeTags);

    void insertLinks(@Param("links") List<Link> links);

    void insertMovies(@Param("movies") List<Movie> movies);

    void insertTags(@Param("tags") List<Tag> tags);
}
