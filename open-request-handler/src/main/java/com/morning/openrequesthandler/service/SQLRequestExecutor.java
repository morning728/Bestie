package com.morning.openrequesthandler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQLRequestExecutor {
    @Value("${application.security.database.task-api-main-db.url}")
    private String mainDatabasePath;
    @Value("${application.security.database.task-api-main-db.username}")
    private String mainDatabaseUsername;
    @Value("${application.security.database.task-api-main-db.password}")
    private String mainDatabasePassword;
    @Value("${application.security.database.security-db.url}")
    private String securityDatabasePath;
    @Value("${application.security.database.security-db.username}")
    private String securityDatabaseUsername;
    @Value("${application.security.database.security-db.password}")
    private String securityDatabasePassword;

    public <T> void executeSQLRequestInMainDB(String query, T... values) throws SQLException {

        Connection connection = DriverManager.getConnection(mainDatabasePath,
                mainDatabaseUsername, mainDatabasePassword);

        PreparedStatement statement = connection.prepareStatement(query);

        String className;
        int indexCounter = 1;
        for (T value : values) {
            className = value.getClass().getSimpleName();
            switch(className) {
                case ("Long"):
                    statement.setLong(indexCounter, (Long) value);
                    break;
                case ("Integer"):
                    statement.setInt(indexCounter, (Integer) value);
                    break;
                case ("String"):
                    statement.setString(indexCounter, (String) value);
                    break;
                case ("Date"):
                    statement.setDate(indexCounter, (Date) value);
                    break;
                case (""):
                    throw new SQLException("Invalid data!");
            }
            indexCounter++;
        }
        int count = statement.executeUpdate();
        System.out.println(count);
        if(count != 1){
            throw new SQLException("Something went wrong...");
        }
    }


    public <T> void executeSQLRequestInSecurityDB(String query, T... values) throws SQLException {

        Connection connection = DriverManager.getConnection(securityDatabasePath,
                securityDatabaseUsername, securityDatabasePassword);

        PreparedStatement statement = connection.prepareStatement(query);

        String className;
        int indexCounter = 1;
        for (T value : values) {
            className = value.getClass().getSimpleName();
            switch(className) {
                case ("Long"):
                    statement.setLong(indexCounter, (Long) value);
                    break;
                case ("Integer"):
                    statement.setInt(indexCounter, (Integer) value);
                    break;
                case ("String"):
                    statement.setString(indexCounter, (String) value);
                    break;
                case ("Date"):
                    statement.setDate(indexCounter, (Date) value);
                    break;
                case (""):
                    throw new SQLException("Invalid data!");
            }
            indexCounter++;
        }
        int count = statement.executeUpdate();
        System.out.println(count);
        if(count != 1){
            throw new SQLException("Something went wrong...");
        }
    }

}
