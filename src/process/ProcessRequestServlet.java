package process;
import javax.servlet.*;
import javax.servlet.http.*;
import database.*;
import java.io.*;
import java.sql.Connection;

public class ProcessRequestServlet extends HttpServlet{
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(req.getRequestURI());
        try {
            Connection c = Postgres.connect();
            out.println(c);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(req.getRequestURI());
    }

}