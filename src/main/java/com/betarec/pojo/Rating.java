package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

public class Rating extends Base {
    public static final String RATING_FILE = "ratings.csv";

    public final int userId;
    public final int movieId;
    public final double rating;
    public final Timestamp timestamp;

    public Rating(String line) {
        String[] v = line.split(",");
        this.userId = Integer.parseInt(v[0]);
        this.movieId = Integer.parseInt(v[1]);
        this.rating = Double.parseDouble(v[2]);
        this.timestamp = new Timestamp(Long.parseLong(v[3]) * 1000L);
    }

    public Rating(int userId, int movieId, double rating, Timestamp timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.timestamp = timestamp;

    }

    public static void buildRatingsDb(ThreadPoolExecutor pool) {
        ParseFile.batchParse(COMMON_FILE_PATH + RATING_FILE, lst -> {
            Resource.batchInsert((dbWriter, lines) -> {
                List<Rating> ratings = lines.stream().map(Rating::new).toList();
                dbWriter.insertRatings(ratings);
            }, lst);
        }, pool);
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        Rating.buildRatingsDb(pool);
        pool.shutdown();
    }
}
