package com.betarec.data;

import com.betarec.data.dao.DbReader;
import com.betarec.data.dao.DbWriter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 利用配置文件mybatis-config.xml 来配置对Mysql的访问接口
 * 搞成单例模式
 *
 * @author pillvic
 */
public class SqlHelper {
    private static final String CONFIG_FILE = "mybatis-config.xml";
    private static final Logger logger = LoggerFactory.getLogger(SqlHelper.class);
    private static SqlSessionManager sqlSessionManager;

    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream(CONFIG_FILE);
            sqlSessionManager = SqlSessionManager.newInstance(inputStream);
            sqlSessionManager.startManagedSession();
            logger.info("SqlHelper init success!");
        } catch (Exception e) {
            logger.error("getDbReader ERROR", e);
        }
    }

    /**
     * 用配置文件获取查询mysql的接口
     */
    public static DbReader getDbReader() {
        return sqlSessionManager.getMapper(DbReader.class);
    }

    public static SqlSessionManager getSqlSessionManager(){
        return sqlSessionManager;
    }


    /**
     * 用配置文件获取写入mysql的接口
     * */
    public static DbWriter getDbWriter() {
        return sqlSessionManager.getMapper(DbWriter.class);
    }
}
