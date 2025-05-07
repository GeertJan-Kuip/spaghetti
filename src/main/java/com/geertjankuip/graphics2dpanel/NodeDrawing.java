package com.geertjankuip.graphics2dpanel;

import java.awt.*;

public class NodeDrawing {

    public NodeDrawing(Graphics2D g2D, NodeObject i, float nodeDrawScaleFactor, int nodeDrawWidth, int nodeDrawHeight, int nodeTextSize, Color nodeColor, Color typeColor1, Color typeColor2 ) {

        int w = (int) (nodeDrawScaleFactor * nodeDrawWidth);
        int h = (int) (nodeDrawScaleFactor * nodeDrawHeight);
        int s = (int) (nodeDrawScaleFactor * nodeTextSize);

        String str = " " + i.name;

        g2D.setColor(nodeColor);
        g2D.setStroke(new BasicStroke( 1 ));

        g2D.drawRect((int) i.x-w/2,(int) i.y-h/2,w,h);
        g2D.setFont(new Font("Courier New", Font.PLAIN, s));
        g2D.drawString(str, (int) i.x-w/2,(int) i.y+2);


        if (i.isInterface==true) {
            g2D.setColor(typeColor1);
        } else {
            g2D.setColor(typeColor2);
        }

        int lw = (int) (4 * nodeDrawScaleFactor);
        g2D.setStroke(new BasicStroke( lw ));
        g2D.drawLine((int) i.x-w/2+3, (int) (i.y + h/2 - lw/2), (int) i.x+w/2-3, (int) (i.y + h/2 - lw/2));
    }

}