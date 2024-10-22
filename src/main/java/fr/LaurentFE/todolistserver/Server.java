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
    private static final String dbCloseErrorMsg = "Error while closing DB connection";

    protected void logRequest(HttpServletRequest request) {
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
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("answering to GET request");
        logRequest(request);
        PrintWriter out = response.getWriter();

        try {
            LOGGER.info("GET {} initiated", request.getPathInfo());
            if (request.getPathInfo().equals("/ToDoList")) {
                out.println(doGetToDoList(request));
            } else if (request.getPathInfo().equals("/ToDoLists")) {
                out.println(doGetToDoLists(request));
            } else {
                LOGGER.info("GET {} attempted but does not exist", request.getPathInfo());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOGGER.error(dbCloseErrorMsg,e);
            throw new RuntimeException(e);
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("answering to POST request");
        logRequest(request);

        try {
            LOGGER.info("POST {} initiated", request.getPathInfo());
            if (request.getPathInfo().equals("/ListItem")) {
                if (doPostListItem(request)) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else if (request.getPathInfo().equals("/ToDoList")) {
                if (doPostToDoList(request)) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else if (request.getPathInfo().equals("/User")) {
                if (doPostUser(request)) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                LOGGER.info("POST {} attempted but does not exist", request.getPathInfo());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOGGER.error(dbCloseErrorMsg, e);
            throw new RuntimeException(e);
        }
    }

    private boolean doPostListItem(HttpServletRequest request) throws SQLException {
        String user_name = request.getParameter("user_name");
        String list_name = request.getParameter("list_name");
        String item_name = request.getParameter("item_name");
        LOGGER.debug("doPostListItem() - user_name={} - list_name={} - item_name={}", user_name, list_name, item_name);

        ToDoListAPI toDoListAPI = new ToDoListAPI();
        boolean res = toDoListAPI.createItemForList(user_name, list_name, item_name);
        toDoListAPI.closeDBConnection();
        return res;
    }

    private boolean doPostToDoList(HttpServletRequest request) throws SQLException {
        String user_name = request.getParameter("user_name");
        String list_name = request.getParameter("list_name");
        LOGGER.debug("doPostToDoList() - user_name={} - list_name={}", user_name, list_name);

        ToDoListAPI toDoListAPI = new ToDoListAPI();
        boolean res = toDoListAPI.createListForUser(user_name, list_name);
        toDoListAPI.closeDBConnection();
        return res;
    }

    private boolean doPostUser(HttpServletRequest request) throws SQLException {
        String user_name = request.getParameter("user_name");
        LOGGER.debug("doPostUser() - user_name={}", user_name);

        ToDoListAPI toDoListAPI = new ToDoListAPI();
        boolean res = toDoListAPI.createUser(user_name);
        toDoListAPI.closeDBConnection();
        return res;
    }
}
