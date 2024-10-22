package fr.LaurentFE.todolistserver;

import fr.LaurentFE.todolistserver.config.ConfigurationManager;
import fr.LaurentFE.todolistserver.config.DBConfig;

import java.sql.*;
import java.util.ArrayList;

public class ToDoListAPI {

    private final Connection connection;

    public ToDoListAPI() {
        ConfigurationManager.getInstance().loadDBConfigurationFile("src/main/resources/db-connection-infos.json");
        DBConfig dbConfig = ConfigurationManager.getInstance().getDbConfig();
        connection = createDBConnection(dbConfig);
    }

    public ToDoList getToDoList(String user_name, String list_name) {
        Integer list_id = getListId(
                getUserId(user_name),
                list_name);
        if (list_id == null) {
            /*
                TODO : Can't find a list with this list_name for user_name (Either here or in getListId())
             */
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

        return lists;
    }

    private Integer getUserId(String userName) {
        String query = "SELECT user_id FROM user WHERE user_name = '" + userName + "';";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            Integer i = null;
            if(rs.next()) {
                i=rs.getInt("user_id");
            }
            /*
                TODO : if there is no user_id, i is NULL, and will not be interpreted as null but as 0 later
             */
            statement.close();
            return i;
        } catch (SQLException e) {
            throw new RuntimeException("SQL Query error : getUserId", e);
        }
    }

    private Integer getListId(Integer user_id, String list_name) {
        String query1 = "SELECT list_id FROM lists WHERE user_id = '"+user_id+"';";
        String query2 = "SELECT list_id FROM list_name WHERE label = '"+list_name+"';";
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
            throw new RuntimeException("SQL Query error : getListId", e);
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
            throw new RuntimeException("SQL Query error : getListItemNames", e);
        }
    }

    private ArrayList<String> getListNames(String user_name) {
        String query = "SELECT label\n" +
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
            throw new RuntimeException("SQL Query error : getListNames", e);
        }
    }

    Connection createDBConnection(DBConfig conf){
        try {
            Connection con = DriverManager.getConnection(
                    conf.getDb_url(),
                    conf.getDb_user(),
                    conf.getDb_pass());

            System.out.println("Connected to database");
            return con;
        }  catch (SQLException e) {
            throw new RuntimeException("MySQL connection error", e);
        }
    }

    public void closeDBConnection() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) {
        ToDoListAPI serv = new ToDoListAPI();


        System.out.println("Getting Bob's to do lists :");

        ArrayList<ToDoList> todolists = serv.getToDoLists("Bob");
        for(ToDoList todolist : todolists) {
            System.out.println("+" + todolist.getLabel());
            for(ListItem item : todolist.getItems()) {
                if (item.isChecked()){
                    System.out.println("X-" + item.getLabel());
                } else {
                    System.out.println("--" + item.getLabel());
                }
            }
        }

        System.out.println("Getting John's list named Groceries :");

        ToDoList johntodo = serv.getToDoList("John", "Groceries");
        if (johntodo!=null) {
            System.out.println("+" + johntodo.getLabel());
            for (ListItem item : johntodo.getItems()) {
                if (item.isChecked()) {
                    System.out.println("X-" + item.getLabel());
                } else {
                    System.out.println("--" + item.getLabel());
                }
            }
        }



        try {
            serv.closeDBConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't properly close DB connection", e);
        }
    }
}
