package com.betarec.utils;

import java.util.regex.Pattern;

public class Flags {
    public static final String COMMON_FILE_PATH = "Data/MovieLens/ml-latest/";
    public static Pattern YEAR_PATTERN = Pattern.compile("[0-9]{4}");
    public static boolean titleContainComma(String line){
        return line.contains("\"");
    }
}
