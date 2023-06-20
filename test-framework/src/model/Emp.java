package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import etu2075.FileUpload;
import etu2075.annotation.Scope;
import etu2075.annotation.Url;
import etu2075.annotation.restAPI;
import etu2075.framework.ModelView;

@Scope("singleton")
public class Emp {
    String nom;
    FileUpload fu;
    int stack = 0;

    public int getStack() {
        return stack;
    }

    public void setStack(Object stack) {
        int st = 0;
        if (stack != null) {
            st = Integer.parseInt(stack.toString());
        }
        this.stack = st;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(Object nom) {
        this.nom = nom.toString();
    }

    public void setFu(Object fu) {
        this.fu = (FileUpload) fu;
    }

    public FileUpload getFu() {
        return fu;
    }

    @Url(url = "emp-find", params = { "id", "name", "len" })
    public ModelView find(Object id, Object name, Object len) {
        int o = Integer.parseInt(id.toString());
        String n = name.toString();
        ModelView mv = new ModelView();
        mv.setView("emplis.jsp");

        HashMap<String, Object> hash = new HashMap<>();
        hash.put("lst", o);
        hash.put("name", n);
        hash.put("len", len);
        mv.setMv(hash);
        return mv;
    }

    @Url(url = "emp-findAll")
    @restAPI
    public ModelView findAll() {
        ModelView mv = new ModelView();
        HashMap<String, Object> hash = new HashMap<>();
        List<HashMap<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> dt = new HashMap<>();
            dt.put("nom", "test" + i);
            dt.put("age", i);
            data.add(dt);
        }
        hash.put("data", data);
        mv.setMv(hash);
        return mv;
    }

    @Url(url = "emp-insert", params = {})
    public ModelView insert() {
        ModelView mv = new ModelView();
        mv.setView("emplis.jsp");
        List<String> ne = new ArrayList<>();
        ne.add("Johary");
        ne.add("Tony");
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("lst", ne);
        mv.setMv(hash);
        return mv;
    }
}
