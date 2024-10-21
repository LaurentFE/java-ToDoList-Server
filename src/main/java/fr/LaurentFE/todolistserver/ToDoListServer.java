package fr.LaurentFE.todolistserver;

import java.sql.*;
import java.util.HashMap;

public class ToDoListServer {

    private final Connection connection;
    Statement statement;

    public ToDoListServer() {
        DBInfoReader dbInfoReader = new DBInfoReader();
        try {
            connection = createDBConnection(dbInfoReader);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<Integer, String> getListNamesForUser(String user_name) {
        String query = "SELECT list_id, label\n" +
                "FROM list_name\n" +
                "WHERE list_id IN (\n" +
                "\tSELECT list_id\n" +
                "\tFROM lists\n" +
                "\tWHERE user_id = (\n" +
                "\t\tSELECT user.user_id \n" +
                "\t\tFROM user \n" +
                "\t\tWHERE user.user_name = '"+user_name+"'\n" +
                "));";
        try {
            ResultSet rs = statement.executeQuery(query);
            HashMap<Integer, String> listNames = new HashMap<>();
            while (rs.next()) {
                int list_id = rs.getInt("list_id");
                String label = rs.getString("label");
                listNames.put(list_id, label);
            }

            return listNames;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Query error : getListNamesFroUser", e);
        }
    }

    Connection createDBConnection(DBInfoReader dbInfoReader){
        try {
            Connection con = DriverManager.getConnection(
                    dbInfoReader.getDb_url(),
                    dbInfoReader.getDb_user(),
                    dbInfoReader.getDb_pass());

            System.out.println("Connected to database");
            return con;
        }  catch (SQLException e) {
            throw new RuntimeException("MySQL connection error", e);
        }
    }

    public static void main(String[] args) {
        ToDoListServer serv = new ToDoListServer();

        HashMap<Integer, String> listNames = serv.getListNamesForUser("Bob");
        for(Integer list_id : listNames.keySet()){
            System.out.println(list_id + " " + listNames.get(list_id));
        }
    }
}
