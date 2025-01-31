package com.geertjankuip.graphics2dpanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDrawing {

    ArrayList<ArrowObject> arrows;

    public ConnectionDrawing(int index, ArrayList<ArrowObject> arrows, Graphics2D g2D, ArrayList<NodeObject> nodes, List<Integer> j, float nodeDrawScaleFactor, int nodeDrawWidth, int nodeDrawHeight, Color nodeColor) {

        this.arrows = arrows;

        double arrowX;
        double arrowY;

        g2D.setColor(nodeColor);
        g2D.setStroke(new BasicStroke( 1 ));

        int x1def;
        int y1def;
        int x2def;
        int y2def;

        double w = (int) (nodeDrawScaleFactor * nodeDrawWidth);
        double h = (int) (nodeDrawScaleFactor * nodeDrawHeight);

        double x1 = nodes.get(j.get(0)).x;
        double y1 = nodes.get(j.get(0)).y;
        double x2 = nodes.get(j.get(1)).x;
        double y2 = nodes.get(j.get(1)).y;

        double dx = x2-x1;
        double dy = y2-y1;
        //double vecLen = Math.sqrt(dx*dx + dy*dy);
        double tan = dy/dx;
        double proportion = h/w;
        if (tan > proportion || tan < -proportion) {
            double dw = 1/tan * h/2;
            double dh = h/2;
            if (y1<y2) {
                x1def = (int) (x1 + dw);
                y1def = (int) (y1 + dh);
                x2def = (int) (x2 - dw);
                y2def = (int) (y2 - dh);
                g2D.drawLine(x1def,y1def,x2def,y2def);
            }else{
                x1def = (int) (x1 - dw);
                y1def = (int) (y1 - dh);
                x2def = (int) (x2 + dw);
                y2def = (int) (y2 + dh);
                g2D.drawLine(x1def,y1def,x2def,y2def);
            }
        } else {
            double dw = w/2;
            double dh = tan * w/2;
            if (x1 < x2) {
                x1def = (int) (x1 + dw);
                y1def = (int) (y1 + dh);
                x2def = (int) (x2 - dw);
                y2def = (int) (y2 - dh);
                g2D.drawLine(x1def, y1def, x2def, y2def);
            } else {
                x1def = (int) (x1 - dw);
                y1def = (int) (y1 - dh);
                x2def = (int) (x2 + dw);
                y2def = (int) (y2 + dh);
                g2D.drawLine(x1def, y1def, x2def, y2def);
            }
        }

        arrowX = (x1def+x2def)/2;
        arrowY = (y1def+y2def)/2;

        //update the arrows array
        arrows.get(index).point.setLocation(arrowX, arrowY);

        // draw arrowpoint
        Polygon polygon = new Polygon();
        Shape shape = polygon;
        AffineTransform aff = new AffineTransform();

        aff.rotate(x1def-x2def, y1def-y2def, arrowX, arrowY);

        polygon.addPoint((int) arrowX + 1, (int) (arrowY + nodeDrawScaleFactor*4+1));
        polygon.addPoint((int) arrowX + 1, (int) (arrowY + -nodeDrawScaleFactor*4-1));
        polygon.addPoint((int) (arrowX + nodeDrawScaleFactor*10+1), (int) arrowY);
        polygon.addPoint((int) arrowX + 1, (int) (arrowY + nodeDrawScaleFactor*4+1));

        shape = aff.createTransformedShape(shape);
        g2D.fill(shape);

        g2D.draw(shape);

    }
}