package model;
import etu2075.annotation.Url;
import etu2075.framework.ModelView;

public class Emp {
    @Url(url="emp-insert")
    public ModelView insert(){
        ModelView mv = new ModelView();
        mv.setView("../emplist.jsp");
        return mv;
    }
}
