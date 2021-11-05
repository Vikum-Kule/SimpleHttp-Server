package com.simplehttpserver.httpserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Database {


    /**
     *
     * @author Vikum
     */
        public static Connection ConnectDB(){
            try{
                Class.forName("org.sqlite.JDBC");
                Connection con= DriverManager.getConnection("jdbc:sqlite:httpserver.db");
                return con;
            }
            catch(ClassNotFoundException | SQLException e){
                System.err.println(e);

                return null;
            }
        }

}
