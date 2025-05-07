package com.geertjankuip.graphics2dpanel;

import java.awt.geom.Point2D;

public class ArrowObject {

    public Point2D.Double point;
    int class1;
    String class1Name;
    int class2;
    String class2Name;

    public ArrowObject(Point2D.Double point, int class1, String class1Name, int class2, String class2Name){

        this.point = point;
        this.class1 = class1;
        this.class1Name = class1Name;
        this.class2 = class2;
        this.class2Name = class2Name;
    }

}