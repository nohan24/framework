package etu2075.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelView {
    String view;
    HashMap<String, Object> mv = new HashMap<>();
    HashMap<String, Object> session = new HashMap<>();
    boolean isJson = false;
    boolean invalidateSession = false;
    boolean _session = false;
    List<String> removeSession = new ArrayList<>();

    public List<String> getRemoveSession() {
        return removeSession;
    }

    public void setRemoveSession(List<String> removeSession) {
        this.removeSession = removeSession;
    }

    public boolean is_session() {
        return _session;
    }

    public void set_session(boolean _session) {
        this._session = _session;
    }

    public boolean isInvalidateSession() {
        return invalidateSession;
    }

    public void setInvalidateSession(boolean invalidateSession) {
        this.invalidateSession = invalidateSession;
    }

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
}
