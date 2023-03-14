package base;

import annotation.Url;

public class Emp {
    @Url(url="emp-insert")
    public String insert(){
        return "insertion";
    }
}
