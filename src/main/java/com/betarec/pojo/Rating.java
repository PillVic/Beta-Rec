package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.sql.Timestamp;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class Rating extends Base {
    public final int userId;
    public final int movieId;
    public final double rating;
    public final Timestamp timestamp;

    public Rating(String line) {
        String[] v = line.split(",");
        this.userId = Integer.parseInt(v[0]);
        this.movieId = Integer.parseInt(v[1]);
        this.rating = Double.parseDouble(v[2]);
        this.timestamp = new Timestamp(Long.parseLong(v[3]));
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH + "ratings.csv", line -> {
            Rating rating = new Rating(line);
            dbWriter.insertRating(rating);
        });
    }
}
