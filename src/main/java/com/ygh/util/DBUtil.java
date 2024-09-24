package com.ygh.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {

    private static Properties props;

    static {
        try {
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
            props = new Properties(); //Properties是存储键值对的集合，用于读取配置文件。
            props.load(in);
            //load方法将InputStream中的内容加载到Properties对象中。application.yml 文件的内容将被解析并存储为键值对。
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从配置文件中读取数据库连接所需的参数，加载相应的数据库驱动
     *
     * @return 数据库连接
     */
    public static Connection getConn() throws Exception {
        String url = props.getProperty("spring.datasource.url");
        String username = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");
        String driver = props.getProperty("spring.datasource.driver-class-name");
        System.out.println("Driver: " + driver);
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
