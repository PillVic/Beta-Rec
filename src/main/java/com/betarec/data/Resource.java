package com.betarec.data;

import com.betarec.config.JavaConfig;
import com.betarec.data.dao.DbReader;
import com.betarec.data.dao.DbWriter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.alibaba.druid.filter.config.ConfigFilter.CONFIG_FILE;

@Component
public class Resource {
    private static final int THREAD_SIZE = 5;
    public final DbWriter dbWriter;
    public final DbReader dbReader;
    public final ThreadPoolExecutor utilPool;
    private SqlSessionManager sqlSessionManager;

    public Resource() {
        this.utilPool = new ThreadPoolExecutor(THREAD_SIZE, THREAD_SIZE * 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(THREAD_SIZE));

        this.sqlSessionManager = SqlHelper.getSqlSessionManager();
        this.dbReader = sqlSessionManager.getMapper(DbReader.class);
        this.dbWriter = sqlSessionManager.getMapper(DbWriter.class);
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
    public void batchInsert(BiConsumer<DbWriter, List<String>> consumer, List<String> lines) {
        consumer.accept(dbWriter, lines);
        sqlSessionManager.commit();
    }
}
