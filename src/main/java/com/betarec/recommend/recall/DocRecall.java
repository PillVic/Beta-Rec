package com.betarec.recommend.recall;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.betarec.index.MovieWrapper.MOVIE_GENRES;
import static com.betarec.index.UserWrapper.SEEN_GENRES_ALL;
import static com.betarec.index.UserWrapper.USER_ID;
import static com.betarec.utils.Flags.MOVIE_GENRES_SPLIT;

public class DocRecall {
    private final IndexSearcher userDocSearcher;
    private final IndexSearcher movieDocSearcher;

    public DocRecall(IndexSearcher userDocSearcher, IndexSearcher movieDocSearcher) {
        this.userDocSearcher = userDocSearcher;
        this.movieDocSearcher = movieDocSearcher;
    }

    public List<Document> movieRecallByUserGenres(int userId, int limit) throws IOException {
        Document userDoc = getUserDocumentByUserId(userId);
        List<String> genres = Arrays.stream(userDoc.get(SEEN_GENRES_ALL).split(MOVIE_GENRES_SPLIT)).toList();
        return recallMoviesByGenres(genres, limit);
    }


    public Document getUserDocumentByUserId(int userId) throws IOException {
        TermQuery query = new TermQuery(new Term(USER_ID, userId + ""));
        BooleanQuery bq = new BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST).build();
        TopDocs docs = userDocSearcher.search(bq, 1);
        return userDocSearcher.doc(docs.scoreDocs[0].doc);
    }

    public List<Document> recallMoviesByGenres(List<String> genres, int limitNum) throws IOException {
        if (!CollectionUtils.isEmpty(genres)) {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            for (var g : genres) {
                builder.add(new TermQuery(new Term(MOVIE_GENRES, g)), BooleanClause.Occur.SHOULD);
            }
            TopDocs docs = movieDocSearcher.search(builder.build(), limitNum);
            if (docs.scoreDocs.length != 0) {
                List<Document> movieDocs = new ArrayList<>();
                for (var doc : docs.scoreDocs) {
                    movieDocs.add(movieDocSearcher.doc(doc.doc));
                }
                return movieDocs;
            }
        }
        return Collections.emptyList();
    }
}
