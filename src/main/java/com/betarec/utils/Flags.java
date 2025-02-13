package com.betarec.utils;

import java.util.regex.Pattern;

public class Flags {
    public static final String GENOME_SCORES = "genome-scores.csv";
    public static final String GENOME_TAGS = "genome-scores.csv";
    public static final String LINK_FILE = "links.csv";
    public static final String MOVIE_FILE = "movies.csv";
    public static final String RATING = "ratings.csv";
    public static final String TAG = "tags.csv";
    public static String MOVIE_GENRES_SPLIT = "\\|";
    public static final String COMMON_FILE_PATH = "ml-latest/";
    public static Pattern YEAR_PATTERN = Pattern.compile("\\([0-9]{4}\\)");
    public static final String EMPTY_GENRES = "(no genres listed)";
    public static final String LOCAL_SERVER = "localhost";

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
