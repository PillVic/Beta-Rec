package com.betarec.data.pojo;

import gen.data.pojo.GenomeScore;
import gen.data.pojo.GenomeTag;
import gen.data.pojo.Link;
import gen.data.pojo.Movie;
import gen.data.pojo.Rating;
import gen.data.pojo.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static com.betarec.utils.Flags.*;

public class PojoParser {

    public static GenomeScore parseGenomeScore(String line) {
        gen.data.pojo.GenomeScore genomeScore = new GenomeScore();

        String[] v = line.split(",");
        genomeScore.movieId = Integer.parseInt(v[0]);
        genomeScore.tagId = Integer.parseInt(v[1]);
        genomeScore.relevance = Double.parseDouble(v[2]);

        return genomeScore;
    }

    public static GenomeTag parseGenomeTag(String line) {
        GenomeTag genomeTag = new GenomeTag();

        String[] v = line.split(",");
        if (v.length != 2) {
            System.out.println(line);
        }
        genomeTag.tagId = Integer.parseInt(v[0]);
        genomeTag.tag = v[1];
        return genomeTag;
    }

    public static Link parseLink(String line) {
        gen.data.pojo.Link link = new Link();
        String[] v = line.split(",");
        link.movieId = Integer.parseInt(v[0]);
        link.imdbId = Integer.parseInt(v[1]);
        link.tmdbId = v.length == 3 ? Integer.parseInt(v[2]) : -1;

        return link;
    }


    public static Movie parseMovie(String line) {
        Movie movie = new Movie();
        String[] v;
        if (titleContainComma(line)) {
            v = line.split("(,\")|(\",)");
        } else {
            v = line.split(",");
        }
        movie.movieId = Integer.parseInt(v[0]);
        if (EMPTY_GENRES.equals(v[2].trim())) {
            movie.genres = "";
        } else {
            movie.genres = v[2].trim();
        }
        Matcher yearMatcher = YEAR_PATTERN.matcher(v[1]);
        int begin = v[1].length();
        if (yearMatcher.find()) {
            String yearStr = yearMatcher.group();
            movie.year = Integer.parseInt(yearStr.substring(1, 5));
            movie.title = v[1].substring(0, v[1].indexOf(yearStr)).trim();
        } else {
            movie.year = -1;
            movie.title = v[1].substring(0, begin).trim();
        }
        return movie;
    }

    public static Rating parseRating(String line){
        gen.data.pojo.Rating rating = new Rating();
        String[] v = line.split(",");
        rating.userId = Integer.parseInt(v[0]);
        rating.movieId = Integer.parseInt(v[1]);
        rating.rating = Double.parseDouble(v[2]);
        rating.timestamp = Long.parseLong(v[3]);
        return  rating;
    }

    public static Tag parseTag(String line){
        Tag tag = new Tag();

        String [] v = line.split(",");
        tag.userId = Integer.parseInt(v[0]);
        tag.movieId = Integer.parseInt(v[1]);
        tag.tag = v[2];
        tag.timestamp = Long.parseLong(v[3]);

        return tag;
    }
}
