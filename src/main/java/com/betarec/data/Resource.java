package com.betarec.data;

import com.betarec.JavaConfig;
import org.apache.ibatis.session.SqlSession;
import org.mariadb.jdbc.internal.util.scheduler.FixedSizedSchedulerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;

@Component
public class Resource {
    private static final int THREAD_SIZE = 5;
    public final DbWriter dbWriter;
    public final DbReader dbReader;
    public final ThreadPoolExecutor utilPool;

    public Resource() {
        this.utilPool = new FixedSizedSchedulerImpl(THREAD_SIZE, "util pool");
        this.dbReader = SqlFactory.getSqlSession(false, true).getMapper(DbReader.class);
        this.dbWriter = SqlFactory.getSqlSession(false, true).getMapper(DbWriter.class);
    }

    public static Resource getResource() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
        return context.getBean("resource", Resource.class);
    }

    /* 对于数据量较大的， 不能一次性提交大事务，而要切割成小事务提交
    * */
    public static void batchInsert(BiConsumer<DbWriter, List<String>> consumer, List<String> lines) {
        SqlSession sqlSession = SqlFactory.getSqlSession(false, false);
        DbWriter dbWriter1 = sqlSession.getMapper(DbWriter.class);
        consumer.accept(dbWriter1, lines);
        sqlSession.commit();
        sqlSession.close();
    }
}
