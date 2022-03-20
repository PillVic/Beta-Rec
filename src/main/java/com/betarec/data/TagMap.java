package com.betarec.data;

import com.betarec.utils.ParseFile;

import java.util.HashMap;
import java.util.Map;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class TagMap {
    private final Map<Integer, String> tagMap;

    private static final String GENOME_TAG_FILE = "genome-tags.csv";

    public TagMap() {
        this.tagMap = new HashMap<>();
        ParseFile.parse(COMMON_FILE_PATH + GENOME_TAG_FILE, line -> {
            String[] v = line.split(",");
            tagMap.put(Integer.parseInt(v[0]), v[1].trim());
        });
    }

    public String getTagName(int tagId) {
        return tagMap.get(tagId);
    }

    public static void main(String[] args) {
        TagMap tagMap = new TagMap();
        System.out.println(tagMap.getTagName(1));
    }
}
