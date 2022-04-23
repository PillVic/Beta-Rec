package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.List;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class GenomeScore extends Base {
    public static final String GNOME_SCORE_FILE = "genome-scores.csv";
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

    public static void buildGenomeScoreDb() {
        ParseFile.batchParse(COMMON_FILE_PATH + GNOME_SCORE_FILE, lst -> {
            Resource.batchInsert((dbWriter, lines) -> {
                List<GenomeScore> genomeScores = lst.stream().map(GenomeScore::new).toList();
                dbWriter.insertGenomeScores(genomeScores);
            },lst);
        });
    }

    public static void main(String[] args) {
        GenomeScore.buildGenomeScoreDb();
    }
}
