package com.betarec.data.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class GenomeScore extends Base {
    public final int movieId;
    public final int tagId;
    public final double relevance;

    public GenomeScore(String line) {
        String[] v = line.split(",");
        movieId = Integer.parseInt(v[0]);
        tagId = Integer.parseInt(v[1]);
        relevance = Double.parseDouble(v[2]);
    }

    public GenomeScore(int movieId, int tagId, double relevance) {
        this.movieId = movieId;
        this.tagId = tagId;
        this.relevance = relevance;
    }
}
