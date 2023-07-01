package etu2075.framework.servlet;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import etu2075.FileUpload;
import etu2075.annotation.Auth;
import etu2075.annotation.Scope;
import etu2075.annotation.Url;
import etu2075.annotation.restAPI;
import etu2075.framework.Mapping;
import etu2075.framework.ModelView;
import utils.PackageTool;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> urlMapping = new HashMap<>();
    HashMap<String, Object> singleton = new HashMap<>();
    String session, profil;

    private boolean checkAuth(HttpServletRequest request, Method method) {
        if (request.getSession().getAttribute(session) == null)
            return false;
        String type = "";
        if (request.getSession().getAttribute(profil) != null) {
            type = request.getSession().getAttribute(profil).toString();
        }
        Auth authentification = method.getAnnotation(Auth.class);
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

    // get all parameters by class field
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

    // check url from link
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

    // reset singleton object attribute
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
        // get the url
        String url = req.getRequestURI();
        url = url.split("/")[url.split("/").length - 1];
        // check if urlmapping contains the current url
        if (urlMapping.containsKey(url)) {
            try {
                Object act = null;
                // singleton
                if (singleton.get(urlMapping.get(url).getClassName()) != null) {
                    act = singleton.get(urlMapping.get(url).getClassName());
                    Method m = act.getClass().getDeclaredMethod("setStack", Object.class);
                    Method a = act.getClass().getDeclaredMethod("getStack");
                    int d = Integer.parseInt(a.invoke(act).toString()) + 1;
                    m.invoke(act, d);
                } else {
                    act = Class.forName(urlMapping.get(url).getClassName()).newInstance();
                }
                // get parameter by the object field's name
                List<String> params = parameter(req, url);
                for (String s : params) {
                    Method m = act.getClass().getDeclaredMethod("set" + capitalize(s), Object.class);
                    m.invoke(act, req.getParameter(s));
                }

                // method from url
                Method curr_method = act.getClass()
                        .getDeclaredMethod(urlMapping.get(url).getMethod(),
                                getParams(check_params(act, urlMapping.get(url).getMethod(), req).length));

                // check if the method need authentification
                if (curr_method.isAnnotationPresent(Auth.class)) {
                    if (!checkAuth(req, curr_method)) {
                        throw new Exception("Need authentification");
                    }
                }

                // initialize the modelview from the called method
                ModelView mv = (ModelView) curr_method.invoke(act,
                        check_params(act, urlMapping.get(url).getMethod(), req));
                // put the object in the modelview's map called mv
                mv.getMv().put("obj", act);

                // check if the method should return a json format
                if (act.getClass().getDeclaredMethod(urlMapping.get(url).getMethod(),
                        getParams(check_params(act, urlMapping.get(url).getMethod(), req).length))
                        .isAnnotationPresent(restAPI.class)) {
                    mv.setJson(true);
                }

                // send the data in the httpserveltrequest's attribute
                for (String key : mv.getMv().keySet()) {
                    req.setAttribute(key, mv.getMv().get(key));
                }

                // initialize session from the modelview session's map
                if (mv.is_session()) {
                    for (String key : mv.getSession().keySet()) {
                        req.getSession().setAttribute(key, mv.getSession().get(key).toString());
                    }
                }
                // upload file
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
                    System.out.println(e);
                }

                // delete session
                if (mv.isInvalidateSession()) {
                    System.out.println("mamafa sessionn");
                    req.getSession().invalidate();
                }

                // delete specific session
                if (mv.getRemoveSession().size() > 0) {
                    for (String s : mv.getRemoveSession()) {
                        req.getSession().setAttribute(s, null);
                    }
                }

                if (!mv.isJson()) {
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher(mv.getView());
                    requestDispatcher.forward(req, res);
                } else {
                    List<HashMap<String, Object>> dt = (List<HashMap<String, Object>>) mv.getMv().get("data");
                    JSONArray j = new JSONArray();
                    for (HashMap<String, Object> hash : dt) {
                        JSONObject jObject = new JSONObject(hash);
                        j.add(jObject);
                    }
                    out.println(j.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeFile(File file, byte[] bytes) {
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            System.out.println("Successfully"
                    + " byte inserted");

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