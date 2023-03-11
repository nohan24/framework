package etu2075.framework.servlet;
import javax.servlet.*;
import javax.servlet.http.*;
import etu2075.framework.Mapping;
import java.io.*;
import java.util.HashMap;

public class FrontServlet extends HttpServlet{
    HashMap<String,Mapping> urlMapping;
    protected void processRequest(HttpServletRequest req,HttpServletResponse res) throws IOException{
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        String url = req.getRequestURI();
        url = url.split("/")[url.split("/").length - 1];
        out.println(url);
        out.println(req.getQueryString());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        processRequest(req, res);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        processRequest(req, res);
    }
}