package com.betarec.data.pojo;

import com.betarec.Base;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.betarec.utils.Flags.COMMON_FILE_PATH;

/**
 * parse movie.csv: movieId, imdbId, tmdbId
 *
 * @author pillvic
 * @date 22-03/13
 */
public class Link extends Base {
    public final int movieId;
    public final int imdbId;
    public final int tmdbId;

    public static final String LINK_FILE = "links.csv";

    public Link(String line) {
        String[] v = line.split(",");
        movieId = Integer.parseInt(v[0]);
        imdbId = Integer.parseInt(v[1]);
        tmdbId = v.length == 3 ? Integer.parseInt(v[2]) : -1;
    }

    public Link(int movieId, int imdbId, int tmdbId) {
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
    }

    @Override
    public int hashCode() {
        return movieId;
    }

    public static void buildLinkDb(ThreadPoolExecutor pool) {
        ParseFile.batchParse(COMMON_FILE_PATH + LINK_FILE, lst -> {
            Resource.getResource().batchInsert((dbWriter, lines) -> {
                List<Link> links = lines.stream().map(Link::new).collect(Collectors.toList());
                dbWriter.insertLinks(links);
            }, lst);
        }, pool);
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = Resource.buildThreadPool();
        buildLinkDb(pool);
        pool.shutdown();
    }
}
