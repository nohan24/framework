package etu2075.framework.servlet;
import javax.servlet.*;
import javax.servlet.http.*;
import etu2075.framework.Mapping;
import java.io.*;
import java.util.HashMap;

public class FrontServlet extends HttpServlet{
    HashMap<String,Mapping> MappingUrls;

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(req.getRequestURI());

    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.println(req.getRequestURI());
    }
}