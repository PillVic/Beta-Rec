package com.betarec.pojo;

import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.sql.Timestamp;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

/**
 * parse movie.csv: userId, movieId, tag,timestamp
 *
 * @author pillvic
 * @date 22-03/29
 */
public class Tag {
    public static final String TAG_FILE = "tags.csv";
    public final int userId;
    public final int movieId;
    public final String tag;
    public final Timestamp timestamp;

    public Tag(String line) {
        String[] v;
        v = line.split("(\"*,\"*(?=[0-9]))|((?<=[0-9])\"*,\"*)");
        this.userId = Integer.parseInt(v[0]);
        this.movieId = Integer.parseInt(v[1]);
        this.tag = v[2];
        this.timestamp = new Timestamp(Long.parseLong(v[3]) * 1000);
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH + TAG_FILE, line -> {
            Tag tag = new Tag(line);
            dbWriter.insertTag(tag);
        });
    }
}
