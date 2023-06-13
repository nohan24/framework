package etu2075.framework.servlet;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import etu2075.FileUpload;
import etu2075.annotation.Auth;
import etu2075.annotation.Scope;
import etu2075.annotation.Url;
import etu2075.framework.Mapping;
import etu2075.framework.ModelView;
import utils.PackageTool;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> urlMapping = new HashMap<>();
    HashMap<String, Object> singleton = new HashMap<>();
    String session, profil;

    private boolean checkAuth(HttpServletRequest request, Method method) {
        Auth authentification = method.getAnnotation(Auth.class);
        if (!method.isAnnotationPresent(Auth.class))
            return false;
        if (request.getSession().getAttribute(session) == null)
            return false;
        if (((boolean) request.getSession().getAttribute(session)) == false)
            return false;

        String type = "";
        if (request.getSession().getAttribute(profil) != null) {
            type = request.getSession().getAttribute(profil).toString();
        }
        if (!authentification.type().equals(type))
            return false;
        return true;
    }

    @Override
    public void init() throws ServletException {
        try {
            for (Class c : PackageTool.inPackage(getServletConfig().getInitParameter("model"))) {
                if (((Scope) c.getAnnotation(Scope.class)).value().equals("singleton")) {
                    singleton.put(c.getName(), c.newInstance());
                }
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(Url.class)) {
                        urlMapping.put(m.getAnnotation(Url.class).url(),
                                new Mapping(c.getName(), m.getAnnotation(Url.class).url().split("-")[1]));
                    }
                }
            }
            session = getServletConfig().getInitParameter("session");
            profil = getServletConfig().getInitParameter("profil");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> parameter(HttpServletRequest req, String cl)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object act = Class.forName(urlMapping.get(cl).getClassName()).newInstance();
        ArrayList<String> ret = new ArrayList<>();
        for (Field f : act.getClass().getDeclaredFields()) {
            if (f.getType() != etu2075.FileUpload.class) {
                if (req.getParameter(f.getName()) != null) {
                    ret.add(f.getName());
                }
            }
        }
        return ret;
    }

    public String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public Object[] check_params(Object obj, String met, HttpServletRequest req) {
        ArrayList<Object> ret = new ArrayList<>();
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Url.class)) {
                if (m.getName().equals(met)) {
                    String[] params = m.getAnnotation(Url.class).params();
                    for (String s : params) {
                        if (req.getParameter(s) != null) {
                            ret.add(req.getParameter(s));
                        }
                    }
                }
            }
        }
        if (ret.size() > 0)
            return ret.toArray();
        return ret.toArray();
    }

    public Class[] getParams(int count) {
        Class[] ret = new Class[count];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Object.class;
        }
        if (count > 0)
            return ret;
        return null;
    }

    protected void reset(Object obj) throws Exception {
        Method m = null;
        for (Field f : obj.getClass().getDeclaredFields()) {
            m = obj.getClass().getDeclaredMethod("set" + capitalize(f.getName()), Object.class);
            m.invoke(obj);
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        String url = req.getRequestURI();
        url = url.split("/")[url.split("/").length - 1];
        if (urlMapping.containsKey(url)) {
            try {
                Object act = null;
                if (singleton.get(urlMapping.get(url).getClassName()) != null) {
                    act = singleton.get(urlMapping.get(url).getClassName());
                    Method m = act.getClass().getDeclaredMethod("setStack", Object.class);
                    Method a = act.getClass().getDeclaredMethod("getStack");
                    int d = Integer.parseInt(a.invoke(act).toString()) + 1;
                    m.invoke(act, d);
                    System.out.println(d);
                } else {
                    act = Class.forName(urlMapping.get(url).getClassName()).newInstance();
                }
                List<String> params = parameter(req, url);
                for (String s : params) {
                    Method m = act.getClass().getDeclaredMethod("set" + capitalize(s), Object.class);
                    m.invoke(act, req.getParameter(s));
                }
                ModelView mv = (ModelView) act.getClass()
                        .getDeclaredMethod(urlMapping.get(url).getMethod(),
                                getParams(check_params(act, urlMapping.get(url).getMethod(), req).length))
                        .invoke(act, check_params(act, urlMapping.get(url).getMethod(), req));
                mv.getMv().put("obj", act);
                for (String key : mv.getMv().keySet()) {
                    req.setAttribute(key, mv.getMv().get(key));
                }
                if (mv._session()) {
                    for (String key : mv.getSession().keySet()) {
                        req.getSession().setAttribute(key, mv.getSession().get(key));
                    }
                }
                try {
                    Collection<Part> parts = req.getParts();
                    if (parts.size() > 0) {
                        for (Field f : act.getClass().getDeclaredFields()) {
                            if (f.getType() == etu2075.FileUpload.class) {
                                Method m = act.getClass().getDeclaredMethod("setFu", f.getType());
                                FileUpload file = fileTraitement(parts, f);
                                m.invoke(act, file);
                                File d = new File("D:/Tomcat/webapps/test-framework/files/" + file.getName());
                                d.createNewFile();
                                writeFile(d, file.getBytes());
                            }
                        }
                    }
                } catch (Exception e) {
                    out.println(e);
                }

                RequestDispatcher requestDispatcher = req.getRequestDispatcher(mv.getView());
                requestDispatcher.forward(req, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeFile(File file, byte[] bytes) {
        try {

            // Initialize a pointer in file
            // using OutputStream
            OutputStream os = new FileOutputStream(file);

            // Starting writing the bytes in it
            os.write(bytes);

            // Display message onconsole for successful
            // execution
            System.out.println("Successfully"
                    + " byte inserted");

            // Close the file connections
            os.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    protected FileUpload fileTraitement(Collection<Part> files, Field field) {
        FileUpload file = new FileUpload();
        String name = field.getName();
        boolean exists = false;
        Part filepart = null;
        for (Part part : files) {
            String contentDisposition = part.getHeader("content-disposition");
            System.out.println(contentDisposition);
            if (part.getName().equals(name)) {
                filepart = part;
                exists = true;
                break;
            }
        }
        try (InputStream io = filepart.getInputStream()) {
            ByteArrayOutputStream buffers = new ByteArrayOutputStream();
            byte[] buffer = new byte[(int) filepart.getSize()];
            int read;
            while ((read = io.read(buffer, 0, buffer.length)) != -1) {
                buffers.write(buffer, 0, read);
            }
            file.setName(this.fileName(filepart));
            file.setBytes(buffers.toByteArray());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String fileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        System.out.println(contentDisposition);
        String[] parts = contentDisposition.split(";");
        for (String partStr : parts) {
            if (partStr.trim().startsWith("filename"))
                return partStr.substring(partStr.indexOf('=') + 1).trim().replace("\"", "");
        }
        return null;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }
}