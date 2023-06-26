package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import etu2075.annotation.Url;
import etu2075.framework.ModelView;

public class Emp {
    String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(Object nom) {
        this.nom = nom.toString();
    }

    @Url(url="emp-find",params = {"id","name","len"})
    public ModelView find(Object id,Object name,Object len){
        int o = Integer.parseInt(id.toString());
        String n = name.toString();
        ModelView mv = new ModelView();
        mv.setView("emplis.jsp");
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("lst", o);
        hash.put("name", n);
        hash.put("len", len);
        
        mv.setMv(hash);
        return mv;
    }

    @Url(url="emp-insert")
    public ModelView insert(){
        ModelView mv = new ModelView();
        mv.setView("emplis.jsp");
        List<String> ne = new ArrayList<>();
        ne.add("Johary");
        ne.add("Tony");
        HashMap<String,Object> hash = new HashMap<>();
        hash.put("lst", ne);
        mv.setMv(hash);
        return mv;
    }
}
