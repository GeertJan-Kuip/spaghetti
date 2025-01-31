package main.java.com.geertjankuip.graphics2dpanel;

public class ClassContainerGraphics {

    int id;
    boolean isinnerclass;
    String representation;
    int nLines;

    public ClassContainerGraphics(int id, boolean isinnerclass, String representation, int nLines){
        this.id=id;
        this.isinnerclass = isinnerclass;
        this.representation = representation;
        this.nLines = nLines;
    }
}