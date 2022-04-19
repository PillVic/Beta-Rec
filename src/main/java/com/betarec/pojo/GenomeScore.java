package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class GenomeScore extends Base {
    public final int movieId;
    public final int tagId;
    public final double relavance;

    public GenomeScore(String line) {
        String[] v = line.split(",");
        movieId = Integer.parseInt(v[0]);
        tagId = Integer.parseInt(v[1]);
        relavance = Double.parseDouble(v[2]);
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH + "genome-scores.csv", line -> {
            //GenomeScore genomeScore = new GenomeScore(line);
            //dbWriter.insertGenomeScore(genomeScore);
        });
    }
}
