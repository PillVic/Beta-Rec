package com.betarec.pojo;

import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class GenomeTag {
    public final int tagId;
    public final String tag;

    public GenomeTag(int tagId, String tag) {
        this.tagId = tagId;
        this.tag = tag;
    }

    public GenomeTag(String line) {
        String[] v = line.split(",");
        this.tagId = Integer.parseInt(v[0]);
        this.tag = v[1];
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH+"genome-tags.csv", line->{
            GenomeTag genomeTag = new GenomeTag(line);
            dbWriter.insertGenomeTag(genomeTag);
        });
    }
}
