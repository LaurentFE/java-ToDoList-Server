package fr.LaurentFE.todolistserver;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;

public class Server extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<html>Bonjour</html>");
    }
}
