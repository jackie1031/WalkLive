package com.WalkLiveApp;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


public class ConnectionHandler {
    public static String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    public static String user = "b0a1d19d87f384";
    public static String password = "6d11c74b";
    private JdbcTemplate jdbcTemplateObject;

    public static BasicDataSource getDataSource(){
        BasicDataSource dbcp = new BasicDataSource();
        dbcp.setDriverClassName("com.mysql.jdbc.Driver");
        dbcp.setUrl(ConnectionHandler.url);
        dbcp.setUsername(ConnectionHandler.user);
        dbcp.setPassword(ConnectionHandler.password);
        return dbcp;
    }

}
