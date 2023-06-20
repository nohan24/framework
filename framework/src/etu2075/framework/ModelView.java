package etu2075.framework;

import java.util.HashMap;

public class ModelView {
    String view;
    HashMap<String, Object> mv = new HashMap<>();
    HashMap<String, Object> session = new HashMap<>();
    boolean isJson = false;

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public HashMap<String, Object> getMv() {
        return mv;
    }

    public void setMv(HashMap<String, Object> mv) {
        this.mv = mv;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean _session() {
        return session != null;
    }
}
