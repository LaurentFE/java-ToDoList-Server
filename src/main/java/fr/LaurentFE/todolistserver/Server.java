package fr.LaurentFE.todolistserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger("root");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("answering to GET request");
        PrintWriter out = response.getWriter();
        String contextPath = "Context path = " + request.getContextPath();
        String servletPath = "Servlet path = " + request.getServletPath();
        String pathInfo = "path info = " + request.getPathInfo();
        String querystring = "query string = " + request.getQueryString();
        String requestUri = "request uri = " + request.getRequestURI();

        LOGGER.debug(contextPath);
        LOGGER.debug(servletPath);
        LOGGER.debug(pathInfo);
        LOGGER.debug(querystring);
        LOGGER.debug(requestUri);

        LOGGER.info("GET {} initiated", request.getPathInfo());
        if (request.getPathInfo().equals("/ToDoList")) {
            try {
                out.println(doGetToDoList(request));
            } catch (SQLException e) {
                LOGGER.error("Error while closing DB connection",e);
                throw new RuntimeException(e);
            }
        } else if (request.getPathInfo().equals("/ToDoLists")) {
            try {
                out.println(doGetToDoLists(request));
            } catch (SQLException e) {
                LOGGER.error("Error while closing DB connection",e);
                throw new RuntimeException(e);
            }
        } else {
            out.println("Hello");
        }
    }

    private String doGetToDoList(HttpServletRequest request) throws SQLException {
        String user_name = request.getParameter("user_name");
        String list_name = request.getParameter("list_name");
        LOGGER.debug("doGetToDoList() - user_name={} - list_name={}", user_name, list_name);
        ToDoListAPI toDoListAPI = new ToDoListAPI();
        ToDoList todo = toDoListAPI.getToDoList(user_name, list_name);
            toDoListAPI.closeDBConnection();
        if (todo == null) {
            return "{}";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(todo, ToDoList.class);
    }

    private String doGetToDoLists(HttpServletRequest request) throws SQLException {
        String user_name = request.getParameter("user_name");
        LOGGER.debug("doGetToDoLists() - user_name={}", user_name);
        ToDoListAPI toDoListAPI = new ToDoListAPI();
        ArrayList<ToDoList> todos = toDoListAPI.getToDoLists(user_name);
        toDoListAPI.closeDBConnection();

        if (todos == null) {
            return "{}";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(todos);
    }
}
