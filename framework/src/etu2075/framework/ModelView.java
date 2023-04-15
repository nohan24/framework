package etu2075.framework;

import java.util.HashMap;

public class ModelView {
    String view;
    HashMap<String,Object> mv = new HashMap<>();

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
