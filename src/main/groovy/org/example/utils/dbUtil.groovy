package org.example.utils

import java.sql.Connection
import java.sql.DriverManager

class dbUtil {

    private static String dbUrl = "jdbc:oracle:thin:@//10.0.0.173:11521/clarity"
    private static String dbUser = "niku"
    private static String dbPassword = "niku"
    private static Connection connection = null

    static Connection connect() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
            println "Connected to the database successfully!"
        } catch (Exception e) {
            e.printStackTrace()
        }
        return connection
    }


    static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close()
                println "Connection closed successfully!"
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
