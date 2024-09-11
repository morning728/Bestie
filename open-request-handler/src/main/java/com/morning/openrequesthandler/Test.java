package com.morning.openrequesthandler;

import org.springframework.beans.factory.annotation.Value;

import java.sql.*;

public class Test {

/*    private static final String ADD_USER_TO_PROJECT_QUERY = """
        INSERT INTO public.user_project (project_id, user_id, role) VALUES (?, ?, 'GUEST')
        """;
    private static <T> void executeSQLRequestInMainDB(String query, T... values) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/task_api_db?serverTimezone=UTC",
                "postgres", "root");

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
//        int count = statement.executeUpdate();
//        if(count != 1){
//            throw new SQLException("Something went wrong...");
//        }
        System.out.println(statement);
    }
    public static void main(String[] args) throws SQLException {
        executeSQLRequestInMainDB(ADD_USER_TO_PROJECT_QUERY, Long.valueOf(12), (long)23);
    }*/
}
