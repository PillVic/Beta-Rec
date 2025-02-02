package com.betarec.data;

import com.betarec.data.dao.DbReader;
import com.betarec.data.dao.DbWriter;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;


@Component
public class Resource {
    private static final int THREAD_SIZE = 5;
    public final DbWriter dbWriter;
    public final DbReader dbReader;
    public final ThreadPoolExecutor utilPool;
    private final SqlSessionManager sqlSessionManager;

    public Resource() {
        this.utilPool = new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(THREAD_SIZE));

        this.sqlSessionManager = SqlHelper.getSqlSessionManager();
        this.dbReader = sqlSessionManager.getMapper(DbReader.class);
        this.dbWriter = sqlSessionManager.getMapper(DbWriter.class);

    }

    public static ThreadPoolExecutor buildThreadPool() {
        return new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(THREAD_SIZE));
    }


    public static IndexSearcher getIndexSearcher(String indexPath) {
        try {
            Directory directory = new NIOFSDirectory(Paths.get(indexPath));
            IndexReader indexReader = DirectoryReader.open(directory);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            return new IndexSearcher(indexReader);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对于数据量较大的， 不能一次性提交大事务，而要切割成小事务提交
     *
     * @author neovic
     */
    public void batchInsert(BiConsumer<DbWriter, List<String>> consumer, List<String> lines) {
        consumer.accept(dbWriter, lines);
        sqlSessionManager.commit();
    }
}
