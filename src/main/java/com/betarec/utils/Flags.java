package com.betarec.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class Flags {
    public static final String COMMON_FILE_PATH = "Data/MovieLens/ml-latest/";
    public static Pattern YEAR_PATTERN = Pattern.compile("[0-9]{4}");

    public static boolean titleContainComma(String line) {
        return line.contains("\"");
    }

    public static enum TABLE {
        //表述所有用到的表名称
        LINKS, MOVIES, TAGS, GENOME_TAGS, RATINGS;
        public String getTableName(){
            return this.name().toLowerCase();
        }
    }
}
