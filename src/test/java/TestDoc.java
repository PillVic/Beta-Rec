import com.betarec.data.Resource;
import com.betarec.recommend.recall.DocRecall;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.betarec.index.MovieWrapper.MOVIE_GENRES;
import static com.betarec.index.MovieWrapper.MOVIE_ID;
import static com.betarec.index.UserWrapper.*;
import static com.betarec.index.builder.MovieIndexBuilder.MOVIE_INDEX_DIR;
import static com.betarec.index.builder.UserIndexBuilder.USER_INDEX_DIR;

public class TestDoc {
    private static final Logger logger = LoggerFactory.getLogger(TestDoc.class);

    private final IndexSearcher userDocSearcher = Resource.getIndexSearcher(USER_INDEX_DIR);
    private final IndexSearcher movieDocSearcher = Resource.getIndexSearcher(MOVIE_INDEX_DIR);
    private final DocRecall docRecall = new DocRecall(userDocSearcher, movieDocSearcher);

    @Test
    public void testGetUserDoc() throws IOException {
        String tag = "Adventure";
        TermQuery query = new TermQuery(new Term(SEEN_GENRES, tag));
        BooleanQuery bq = new BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST).build();
        TopDocs docs = userDocSearcher.search(bq, 20);
        for (var doc : docs.scoreDocs) {
            Document document = userDocSearcher.doc(doc.doc);
            logger.info("userId:{}, seen_genres:{}", document.get(USER_ID), document.get(SEEN_GENRES_ALL));
        }

    }

    @Test
    public void testGetUserDocByUserId() throws IOException {
        int userId = 1231;
        Document document = docRecall.getUserDocumentByUserId(userId);
        logger.info("userId:{}, seen_genres:{}", document.get(USER_ID), document.get(SEEN_GENRES_ALL));
    }

    @Test
    public void testRecallMovieByGenres() throws IOException {
        List<String> genres = List.of("Action");
        int limit = 20;
        List<Document> documents = docRecall.recallMoviesByGenres(genres, limit);
        for (Document document : documents) {
            logger.info("movieId:{}, movie genres:{}", document.get(MOVIE_ID), document.get(MOVIE_GENRES));
        }
    }

    @Test
    public void testGetMovieDoc() throws IOException {
        String tag = "Adventure";
        TermQuery query = new TermQuery(new Term(MOVIE_GENRES, tag));
        BooleanQuery bq = new BooleanQuery.Builder().add(query, BooleanClause.Occur.MUST).build();
        TopDocs docs = movieDocSearcher.search(bq, 20);
        for (var doc : docs.scoreDocs) {
            Document document = movieDocSearcher.doc(doc.doc);
            logger.info("movieId:{}, movie_genres:{}", document.get(MOVIE_ID), document.get(MOVIE_GENRES));
        }
    }

    @Test
    public void testMovieRecallByUserGenres() throws IOException {
        int userId = 401;
        int limit = 10;
        List<Document> movieDocs = docRecall.movieRecallByUserGenres(userId, limit);
        for(Document document:movieDocs){
            logger.info("movieId:{}, movie genres:{}", document.get(MOVIE_ID), document.get(MOVIE_GENRES));
        }
    }
}
