package main.java.com.geertjankuip.graphics2dpanel;

import main.java.com.geertjankuip.gui.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Graphics2DPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    ArrayList<NodeObject> nodes;
    Set<List<Integer>> connections;
    ArrayList<ArrowObject> arrows;

    int nodeDrawWidth = 120;
    int nodeDrawHeight = 20;
    int nodeTextSize = 12;
    Color nodeColor = new Color(20,20,20);
    Color typeColor1 = new Color(32, 208, 32);
    Color typeColor2 = new Color(133, 8, 8);

    float nodeDrawScaleFactor = 1.0F;

    int graphics2DPanelWidth;
    int graphics2DPanelHeight;

    Graphics2DCalculations graphics2DCalculations;
    Controller controller;

    NodeObject closeNode;
    ArrowObject closeArrow;

    Timer t = new Timer(20, this);

    public Graphics2DPanel(ArrayList<NodeObject> nodes, Set<List<Integer>> connections, ArrayList<ArrowObject> arrows, Graphics2DCalculations drawCalculations, int graphics2DPanelWidth, int graphics2DPanelHeight, Controller controller){

        this.nodes=nodes;
        this.connections=connections;
        this.arrows = arrows;
        this.graphics2DCalculations = drawCalculations;
        this.graphics2DPanelWidth = graphics2DPanelWidth;
        this.graphics2DPanelHeight = graphics2DPanelHeight;
        this.controller = controller;

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected void paintComponent(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;

        g2D.translate(graphics2DPanelWidth / 2, graphics2DPanelHeight / 2);

        g2D.clearRect(-graphics2DPanelWidth /2 ,-graphics2DPanelHeight /2  , graphics2DPanelWidth, graphics2DPanelHeight);

        g2D.setColor(Color.lightGray);

        g2D.drawRect(-graphics2DPanelWidth / 2 + 5, -graphics2DPanelHeight /2 + 5, graphics2DPanelWidth -10, graphics2DPanelHeight -10);

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (NodeObject i : nodes){

            new NodeDrawing(g2D, i, nodeDrawScaleFactor, nodeDrawWidth, nodeDrawHeight,nodeTextSize,nodeColor, typeColor1, typeColor2);
        }

        int index = 0;
        for (List<Integer> j: connections){

            new ConnectionDrawing(index, arrows, g2D,nodes,j,nodeDrawScaleFactor,nodeDrawWidth,nodeDrawHeight,nodeColor);
            index = index+1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println("Mouse Clicked");
        // returnvalue is an array { closeNode, closeArrow, which of the two is closest }
        closeArrow = (ArrowObject) graphics2DCalculations.getNearestArrow(e.getX() - graphics2DPanelWidth /2, e.getY() - graphics2DPanelHeight /2).get(0);
        String nearest = graphics2DCalculations.getNearestNodeOrArrow(e.getX() - graphics2DPanelWidth /2, e.getY() - graphics2DPanelHeight /2);
        if (nearest==null){
            System.out.println("Null value returned. Couldn't determine what was closest");
        }else if (nearest == "Node"){
            System.out.println("A Node was returned");
            System.out.println("It's name is " + closeNode.name);
        }else if (nearest == "Arrow"){
            System.out.println("An Arrow was returned");
            System.out.println("It connects classes " + closeArrow.class1Name + " and " + closeArrow.class1Name);
            controller.setJTextPaneFromDB(closeArrow.class2+1, "Top");
            controller.setJTextPaneFromDB(closeArrow.class1+1, "Bottom");
        }else {
            System.out.println("Don't know what the return value was but not null, Node or Arrow");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        t.start();
        closeNode = (NodeObject) graphics2DCalculations.getNearestNode(e.getX() - graphics2DPanelWidth /2, e.getY() - graphics2DPanelHeight /2).get(0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        t.stop();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        closeNode.x = e.getX()- graphics2DPanelWidth /2;
        closeNode.y = e.getY()- graphics2DPanelHeight /2;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}