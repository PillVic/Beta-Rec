package com.betarec.data;

import com.betarec.pojo.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 相关数据的读操作
 * 由于genome_scores, ratings的数据量过大，要分表， 读操作方法位于Split
 */
public interface DbReader {
    @MapKey("movieId")
    Map<Integer, Movie> getMovies(@Param("movieIds") List<Integer> movieIds);

    @MapKey("movieId")
    Map<Integer, Link> getLinks(@Param("movieIds") List<Integer> movieIds);

    @MapKey("tagId")
    Map<Integer, GenomeTag> getGenomeTags(@Param("tagIds") List<Integer> genomeTagIds);
}
