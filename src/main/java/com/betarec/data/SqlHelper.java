package com.betarec.data;

import com.betarec.data.dao.DbReader;
import com.betarec.data.dao.DbWriter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 利用配置文件mybatis-config.xml 来配置对Mysql的访问接口
 * @author pillvic
* */
public class SqlHelper {
    private static final String CONFIG_FILE = "mybatis-config.xml";
    private static final Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    /**
     * 用配置文件获取查询mysql的接口
    * */
    public static DbReader getDbReader() {
        try {
            InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSessionManager.startManagedSession();


            return sqlSessionManager.getMapper(DbReader.class);
        } catch (Exception e) {
            logger.error("getDbReader ERROR", e);
        }
        return null;
    }

    public static SqlSessionManager getSqlSessionManager(){
        try {
            InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSessionManager.startManagedSession();

            return sqlSessionManager;
        } catch (Exception e) {
            logger.error("getSqlSessionManager ERROR", e);
        }
        return null;
    }


    /**
     * 用配置文件获取写入mysql的接口
     * */
    public static DbWriter getDbWriter(boolean realWrite) {
        try {
            InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
            SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSessionManager.startManagedSession();

            sqlSessionManager.getMapper(DbWriter.class);
            return sqlSessionManager.getMapper(DbWriter.class);
        } catch (Exception e) {
            logger.error("getDbWriter ERROR", e);
        }
        return null;
    }
}
