package com.universityadministration.service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class InjectSqlCommand {

    private String myDriver = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/universityadministration";
    private String user = "root";
    private String pass = "ASas1234$";
    private String sql;
    private Connection connection = null;
    private ResultSet resultSet = null;
    private Statement statement = null;
    public void addUserToAuthority(long userId, String authorityName){
        int authorityId = getAuthorityIdByName(authorityName);
        sql = "INSERT INTO user_authority(user_id, authority_id) VALUES("+userId+","+authorityId+")";
        try
        {
            Class.forName(myDriver);
            connection = DriverManager.getConnection(url,user,pass);
            Statement st = connection.createStatement();
            st.executeUpdate(sql);
            connection.close();
        }
        catch(Exception ex)
        {
            System.err.println(ex);
        }
    }

    public int getAuthorityIdByName(String authorityName){
        sql = "select * from universityadministration.authority";
        try
        {
            Class.forName(myDriver);
            connection = DriverManager.getConnection(url,user,pass);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("authority_id");
                String name = resultSet.getString("authority_name");
                if(name.equals(authorityName)){
                    connection.close();
                    return id;
                }
            }
            connection.close();
        }
        catch(Exception ex)
        {
            System.err.println(ex);
        }
        return 0;
    }
}