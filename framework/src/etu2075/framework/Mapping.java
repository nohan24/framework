package etu2075.framework;

public class Mapping {
    String className,Method;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public Mapping(String className,String Method){
        this.className = className;
        this.Method = Method;
    }
}

