package fr.LaurentFE.todolistserver;

import fr.LaurentFE.todolistserver.config.ConfigurationManager;
import fr.LaurentFE.todolistserver.config.DBConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class ToDoListAPI {

    private final Connection connection;
    private static final Logger LOGGER = LogManager.getLogger("root");

    public ToDoListAPI() {
        ConfigurationManager.getInstance().loadDBConfigurationFile("db-connection-infos.json");
        DBConfig dbConfig = ConfigurationManager.getInstance().getDbConfig();
        connection = createDBConnection(dbConfig);
    }

    public ToDoList getToDoList(String user_name, String list_name) {
        Integer list_id = getListId(
                getUserId(user_name),
                list_name);
        if (list_id == null || list_id == 0) {
            LOGGER.error("{} list does not exist for user {}", list_name, user_name);
            return null;
        }
        return new ToDoList(list_id, list_name, getListItems(list_id));
    }

    public ArrayList<ToDoList> getToDoLists(String user_name) {
        ArrayList<String> list_names = getListNames(user_name);
        ArrayList<ToDoList> lists = new ArrayList<>();
        for( String list_name : list_names) {
            lists.add(getToDoList(user_name, list_name));
        }
        if (lists.isEmpty()){
            return null;
        } else {
            return lists;
        }
    }

    private Integer getUserId(String userName) {
        String query = "SELECT user_id FROM users WHERE user_name = '" + userName + "';";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            Integer i = null;
            if(rs.next()) {
                i=rs.getInt("user_id");
            }
            statement.close();
            return i;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : getUserId";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    private Integer getListId(Integer user_id, String list_name) {
        String query1 = "SELECT list_id FROM lists WHERE user_id = '"+user_id+"';";
        String query2 = "SELECT list_id FROM list_names WHERE label = '"+list_name+"';";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query1);
            ArrayList<Integer> user_list_ids = new ArrayList<>();
            while(rs.next()) {
                user_list_ids.add(rs.getInt("list_id"));

            }
            ArrayList<Integer> name_list_ids = new ArrayList<>();
            rs = statement.executeQuery(query2);
            while(rs.next()) {
                name_list_ids.add(rs.getInt("list_id"));
            }

            ArrayList<Integer> list_ids = new ArrayList<>(user_list_ids);
            list_ids.retainAll(name_list_ids);

            statement.close();
            return !list_ids.isEmpty() ? list_ids.getFirst() : null;

        } catch (SQLException e) {
            String error_msg = "SQL Query error : getListId";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    private ArrayList<ListItem> getListItems(Integer list_id) {
        String query = "SELECT item_id, label, is_checked\n" +
                "FROM items\n" +
                "WHERE item_id IN (\n" +
                "\tSELECT item_id\n" +
                "\tFROM list_items\n" +
                "\tWHERE list_id = "+list_id+"\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ArrayList<ListItem> listItems = new ArrayList<>();

            while (rs.next()) {
                Integer item_id = rs.getInt("item_id");
                String label = rs.getString("label");
                Boolean is_checked = rs.getBoolean("is_checked");
                listItems.add(
                        new ListItem(item_id, label, is_checked));
            }

            statement.close();
            return listItems;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : getListItemNames";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    private ArrayList<String> getListNames(String user_name) {
        String query = "SELECT label\n" +
                "FROM list_names\n" +
                "WHERE list_id IN (\n" +
                "\tSELECT list_id\n" +
                "\tFROM lists\n" +
                "\tWHERE user_id = (\n" +
                "\t\tSELECT users.user_id \n" +
                "\t\tFROM users \n" +
                "\t\tWHERE users.user_name = '"+user_name+"'\n" +
                "));";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ArrayList<String> listNames = new ArrayList<>();
            while (rs.next()) {
                String label = rs.getString("label");
                listNames.add(label);
            }

            statement.close();
            return listNames;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : getListNames";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    Connection createDBConnection(DBConfig conf){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    conf.getDb_url(),
                    conf.getDb_user(),
                    conf.getDb_pass());

            System.out.println("Connected to database");
            return con;
        }  catch (SQLException e) {
            String error_msg = "MySQL connection error";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeDBConnection() throws SQLException {
        connection.close();
    }
}
