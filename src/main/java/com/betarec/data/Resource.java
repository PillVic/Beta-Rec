package com.betarec.data;

import com.betarec.JavaConfig;
import org.mariadb.jdbc.internal.util.scheduler.FixedSizedSchedulerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
public class Resource {
    private static final int THREAD_SIZE = 5;
    public final DbWriter dbWriter;
    public final DbReader dbReader;
    public final ThreadPoolExecutor utilPool;

    public Resource(){
        this.utilPool = new FixedSizedSchedulerImpl(THREAD_SIZE, "util pool");
        this.dbReader = SqlFactory.getSqlSession(false).getMapper(DbReader.class);
        this.dbWriter = SqlFactory.getSqlSession(true).getMapper(DbWriter.class);
    }
    public static Resource getResource(){
        ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
        return context.getBean("resource", Resource.class);
    }
}
