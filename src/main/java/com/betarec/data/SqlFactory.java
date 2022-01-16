package com.betarec.data;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 利用配置文件mybatis-config.xml 来配置对Mysql的访问接口
 * @author pillvic
* */
public class SqlFactory {
    private static final String CONFIG_FILE = "mybatis-config.xml";
    private static SqlSessionFactory sqlSessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(SqlFactory.class);
    static{
        try{
            InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }catch (Exception e){
            logger.error("SqlFactory ERROR:", e);
        }
    }
    public static SqlSession getSqlSession(boolean realWrite){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            if(realWrite){
                sqlSession.commit();
            }
            sqlSession.close();
        }));
        return sqlSession;
    }
}
