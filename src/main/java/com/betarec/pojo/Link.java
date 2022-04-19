package com.betarec.pojo;

import com.betarec.Base;
import com.betarec.data.DbWriter;
import com.betarec.data.Resource;
import com.betarec.utils.ParseFile;

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
    public Link(int movieId, int imdbId, int tmdbId){
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
    }

    public static void main(String[] args) {
        DbWriter dbWriter = Resource.getResource().dbWriter;
        ParseFile.parse(COMMON_FILE_PATH + LINK_FILE, line -> {
            Link link = new Link(line);
            dbWriter.insertLink(link);
        });
    }
}
