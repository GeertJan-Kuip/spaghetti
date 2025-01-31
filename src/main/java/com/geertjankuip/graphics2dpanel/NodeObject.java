package main.java.com.geertjankuip.graphics2dpanel;

public class NodeObject {

    public double x;
    public double y;
    public int size;
    public double forceX;
    public double forceY;
    public int classID;
    boolean isInterface;
    public String name;

    public NodeObject(double x, double y, int size, int classID, boolean isInterface, String name) {

        this.x = x;
        this.y = y;
        this.size = size;
        this.classID = classID;
        this.isInterface = isInterface;
        this.name = name;
    }
}