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

    public static void buildDb(){
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.batchParse(COMMON_FILE_PATH + GNOME_SCORE_FILE, lines -> {
            List<GenomeScore> genomeScores = lines.stream().map(GenomeScore::new).toList();
            dbWriter.insertGenomeScores(genomeScores);
        });
    }

    public static void main(String[] args) {
        GenomeScore.buildDb();
    }
}
