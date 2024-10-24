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

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        String query = """
                SELECT user_id, user_name
                FROM users;
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getInt("user_id"), rs.getString("user_name"));
                users.add(user);
            }

            statement.close();
            return users;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : getUsers";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    public boolean createUser(String user_name) {
        try {
            String query = "INSERT INTO users (user_name) VALUE (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user_name);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : createUser";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    public boolean createListForUser(String user_name, String list_name) {
        Integer user_id = getUserId(user_name);
        if (user_id == null || user_id == 0) {
            LOGGER.error("{} user does not exist", user_name);
            return false;
        }
        try {
            String query1 = "INSERT INTO lists (user_id) VALUES (?);";
            String query2 = "SELECT LAST_INSERT_ID();";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, user_id);
            statement1.executeUpdate();
            statement1.close();

            PreparedStatement statement2 = connection.prepareStatement(query2);
            ResultSet rs = statement2.executeQuery(query2);
            Integer list_id = null;
            if(rs.next()) {
                list_id = rs.getInt(1);
                statement2.close();
                String query3 = "INSERT INTO list_names (list_id, label) VALUES (?, ?);";
                PreparedStatement statement3 = connection.prepareStatement(query3);
                statement3.setInt(1, list_id);
                statement3.setString(2, list_name);
                statement3.executeUpdate();
                statement3.close();
                return true;
            } else {
                LOGGER.error("createListForUser: couldn't fetch LAST_INSERT_ID() for list_name {}", list_name);
                return false;
            }
        } catch (SQLException e) {
            String error_msg = "SQL Query error : createListForUser";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    public boolean createItemForList(String user_name, String list_name, String item_name) {
        Integer user_id = getUserId(user_name);
        Integer list_id = getListId(user_id, list_name);
        if (list_id == null || list_id == 0 || user_id == null || user_id == 0) {
            LOGGER.info("Can't insert item={} in list={} for user {} : list or user doesn't exist", item_name, list_name, user_name);
            return false;
        }
        try {
            String query1 = "INSERT INTO items (label) VALUE (?);";
            String query2 = "SELECT LAST_INSERT_ID();";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setString(1, item_name);
            statement1.executeUpdate();
            statement1.close();

            PreparedStatement statement2 = connection.prepareStatement(query2);
            ResultSet rs = statement2.executeQuery(query2);
            Integer item_id = null;
            if (rs.next()) {
                item_id = rs.getInt(1);
                statement2.close();
                String query3 = "INSERT INTO list_items (list_id, item_id) VALUES (?, ?);";
                PreparedStatement statement3 = connection.prepareStatement(query3);
                statement3.setInt(1, list_id);
                statement3.setInt(2, item_id);
                statement3.executeUpdate();
                statement3.close();
                return true;
            } else {
                LOGGER.error("createItemForList: couldn't fetch LAST_INSERT_ID() for item {}", item_name);
                return false;
            }
        } catch (SQLException e) {
            String error_msg = "SQL Query error : createItemForList";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    public boolean updateToDoListName(String user_name, String list_name, String new_list_name) {
        Integer user_id = getUserId(user_name);
        Integer list_id = getListId(user_id, list_name);
        if (list_id == null || list_id == 0 || user_id == null || user_id == 0) {
            LOGGER.info("Can't update list={} to new_list={} for user {} : list or user doesn't exist", list_name, new_list_name, user_name);
            return false;
        }
        try {
            String query = "UPDATE list_names SET label = ? WHERE (list_id = ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, new_list_name);
            statement.setInt(2, list_id);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : updateToDoListName";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    public boolean updateListItemName(String user_name, String list_name, String item_name, String new_item_name) {
        Integer user_id = getUserId(user_name);
        Integer list_id = getListId(user_id, list_name);
        Integer item_id = getItemId(list_id, item_name);
        if (list_id == null || list_id == 0 || user_id == null || user_id == 0 || item_id == null || item_id == 0) {
            LOGGER.info("Can't update item={} to new_item={} for list {} user {}: item, list or user doesn't exist", item_name, new_item_name, list_name, user_name);
            return false;
        }
        try {
            String query = "UPDATE items SET label = ? WHERE (item_id = ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, new_item_name);
            statement.setInt(2, item_id);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : updateToDoListName";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    public boolean updateListItemCheck(String user_name, String list_name, String item_name, String isChecked) {
        Integer user_id = getUserId(user_name);
        Integer list_id = getListId(user_id, list_name);
        Integer item_id = getItemId(list_id, item_name);
        if (list_id == null || list_id == 0 || user_id == null || user_id == 0 || item_id == null || item_id == 0) {
            LOGGER.info("Can't update item={} to check={} for list {} user {}: item, list or user doesn't exist", item_name, isChecked, list_name, user_name);
            return false;
        }
        try {
            String query = "UPDATE items SET is_checked = ? WHERE (item_id = ?);";
            PreparedStatement statement = connection.prepareStatement(query);
            if (isChecked.equalsIgnoreCase("true")) {
                statement.setBoolean(1, true);
            } else if (isChecked.equalsIgnoreCase("false")) {
                statement.setBoolean(1, false);
            } else {
                LOGGER.info("updateListItemCheck: Incorrect value for PUT isChecked param");
                statement.close();
                return false;
            }
            statement.setInt(2, item_id);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            String error_msg = "SQL Query error : updateToDoListName";
            LOGGER.error(error_msg,e);
            return false;
        }
    }

    private Integer getUserId(String userName) {
        String query = "SELECT user_id FROM users WHERE user_name = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            ResultSet rs = statement.executeQuery();
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
        String query1 = "SELECT list_id FROM lists WHERE user_id = ?;";
        String query2 = "SELECT list_id FROM list_names WHERE label = ?;";
        try {
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, user_id);
            ResultSet rs = statement1.executeQuery();
            ArrayList<Integer> user_list_ids = new ArrayList<>();
            while(rs.next()) {
                user_list_ids.add(rs.getInt("list_id"));

            }
            statement1.close();
            ArrayList<Integer> name_list_ids = new ArrayList<>();
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setString(1, list_name);
            rs = statement2.executeQuery();
            while(rs.next()) {
                name_list_ids.add(rs.getInt("list_id"));
            }

            ArrayList<Integer> list_ids = new ArrayList<>(user_list_ids);
            list_ids.retainAll(name_list_ids);

            statement2.close();
            return !list_ids.isEmpty() ? list_ids.getFirst() : null;

        } catch (SQLException e) {
            String error_msg = "SQL Query error : getListId";
            LOGGER.error(error_msg,e);
            throw new RuntimeException(error_msg, e);
        }
    }

    private Integer getItemId(Integer list_id, String item_name) {
        String query = """
                SELECT items.item_id
                FROM list_items JOIN items
                WHERE (items.item_id = list_items.item_id
                \tAND list_items.list_id = ?
                \tAND items.label = ?);
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, list_id);
            statement.setString(2, item_name);
            ResultSet rs = statement.executeQuery();
            Integer item_id = null;
            if (rs.next()) {
                item_id = rs.getInt(1);
            }
            statement.close();
            return item_id;
        } catch (SQLException e) {
                String error_msg = "SQL Query error : getItemId";
                LOGGER.error(error_msg,e);
                throw new RuntimeException(error_msg, e);
        }
    }

    private ArrayList<ListItem> getListItems(Integer list_id) {
        String query = """
                SELECT item_id, label, is_checked
                FROM items
                WHERE item_id IN (
                \tSELECT item_id
                \tFROM list_items
                \tWHERE list_id = ?
                );""";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, list_id);
            ResultSet rs = statement.executeQuery();
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
        String query = """
                SELECT label
                FROM list_names
                WHERE list_id IN (
                \tSELECT list_id
                \tFROM lists
                \tWHERE user_id = (
                \t\tSELECT users.user_id\s
                \t\tFROM users\s
                \t\tWHERE users.user_name = ?
                )) ORDER BY label;""";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user_name);
            ResultSet rs = statement.executeQuery();
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

            LOGGER.info("Connection to DB successful");
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
        LOGGER.info("Disconnection from DB successful");
        connection.close();
    }
}
