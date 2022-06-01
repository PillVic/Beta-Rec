package com.betarec.data;

import com.betarec.JavaConfig;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

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

    public Resource() {
        this.utilPool = new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(THREAD_SIZE));
        this.dbReader = SqlFactory.getSqlSession(false, true).getMapper(DbReader.class);
        this.dbWriter = SqlFactory.getSqlSession(true, true).getMapper(DbWriter.class);
    }

    public static Resource getResource() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
        return context.getBean("resource", Resource.class);
    }
    public static ThreadPoolExecutor buildThreadPool(){
        return new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(THREAD_SIZE));
    }


    /**
     * 对于数据量较大的， 不能一次性提交大事务，而要切割成小事务提交
     *
     * @author neovic
     */
    public static void batchInsert(BiConsumer<DbWriter, List<String>> consumer, List<String> lines) {
        SqlSession sqlSession = SqlFactory.getSqlSession(false, false);
        DbWriter dbWriter1 = sqlSession.getMapper(DbWriter.class);
        consumer.accept(dbWriter1, lines);
        sqlSession.commit();
        sqlSession.close();
    }
}
