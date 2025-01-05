package com.betarec.data.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

/**
 * parse genomeTag.csv: tagId, tag
 *
 * @author pillvic
 * @date 22-03/13
 */
public class GenomeTag extends Base {
    public final int tagId;
    public final String tag;

    public static final String GENOME_TAG_FILE = "genome-tags.csv";

    public GenomeTag(int tagId, String tag) {
        this.tagId = tagId;
        this.tag = tag;
    }

    public GenomeTag(String line) {
        String[] v = line.split(",");
        if (v.length != 2) {
            System.out.println(line);
        }
        this.tagId = Integer.parseInt(v[0]);
        this.tag = v[1];
    }

    @Override
    public int hashCode() {
        return tagId;
    }
}
