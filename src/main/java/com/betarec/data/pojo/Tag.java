package com.betarec.data.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

/**
 * parse movie.csv: userId, movieId, tag,timestamp
 *
 * @author pillvic
 * @date 22-03/29
 */
public class Tag extends Base {
    public static final String TAG_FILE = "tags.csv";
    public final int userId;
    public final int movieId;
    public final String tag;
    public final Timestamp timestamp;

    public Tag(String line) {
        List<String> v = new ArrayList<>();
        int firstComma = line.indexOf(",");
        int secondComma = line.indexOf(",", firstComma + 1);
        int lastComma = line.lastIndexOf(",");
        v.add(line.substring(0, firstComma));
        v.add(line.substring(firstComma + 1, secondComma));
        v.add(line.substring(secondComma + 1, lastComma));
        v.add(line.substring(lastComma + 1));
        this.userId = Integer.parseInt(v.get(0));
        this.movieId = Integer.parseInt(v.get(1));
        this.tag = v.get(2).replace("\"", "");
        this.timestamp = new Timestamp(Long.parseLong(v.get(3)) * 1000);
    }

    public Tag(int userId, int movieId, String tag, Timestamp timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    public static void buildTagDb(ThreadPoolExecutor pool) {
        ParseFile.batchParse(COMMON_FILE_PATH + TAG_FILE, lst -> {
            Resource.batchInsert((dbWriter, lines) -> {
                List<Tag> tags = lines.stream().map(Tag::new).collect(Collectors.toList());
                dbWriter.insertTags(tags);
            }, lst);
        }, pool);
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildTagDb(pool);
        pool.shutdown();
    }
}
