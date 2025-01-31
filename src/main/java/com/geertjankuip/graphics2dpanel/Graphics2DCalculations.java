package com.geertjankuip.graphics2dpanel;

import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.sqlite.SQLiteReader;

import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graphics2DCalculations {

    ArrayList<NodeObject> nodes = new ArrayList<>();
    Set<List<Integer>> connections = new HashSet<>();
    ArrayList<ArrowObject> arrows = new ArrayList<ArrowObject>();

    NodeObject closeNode;
    ArrowObject closeArrow;

    int nNodes;
    int nConnections;
    float gravityConstant = 1.1F;
    int repulsionConstant = 5000;
    int startDisMultiplier = 1;
    double pullFactor = 0.1;

    ActivityLogger logger;

    public Graphics2DCalculations(ActivityLogger logger){
        this.logger = logger;
    }

    public ArrayList<NodeObject> getNodesFromDB(int graphics2DPanelWidth, int graphics2DPanelHeight){

        ArrayList<ClassContainerGraphics> classData;
        Set<List<Integer>> classRelationsData;

        SQLiteReader sqlite  = new SQLiteReader(logger);
        try {
            classData = sqlite.getClassDataForGraphics();
        }catch(SQLException e) {
            throw new RuntimeException();
        }

        nNodes = classData.size();

        for (ClassContainerGraphics cc: classData) {

            double posx = 0;
            double posy = 0;
            int size = cc.nLines ;
            int classID = cc.id;
            boolean isInterface = cc.isinnerclass;
            String name = cc.representation;

            NodeObject n = new NodeObject(posx, posy,size,classID, isInterface,name);
            nodes.add(n);
        }

        for (NodeObject cc: nodes) {

            double posx = ((Math.random() * 2 - 1) * graphics2DPanelHeight/2 * startDisMultiplier);
            double posy = ((Math.random() * 2 - 1) * graphics2DPanelWidth/2 * startDisMultiplier);

            cc.x = posx;
            cc.y = posy;
        }

        return(nodes);
    }

    public Set<List<Integer>> getConnectionsDataFromDB() {
        SQLiteReader sqlite  = new SQLiteReader(logger);
        try {
            connections = sqlite.getClassRelationsDataForGraphics();
        }catch(SQLException e) {
            throw new RuntimeException();
        }
        nConnections = connections.size();

        return(connections);
    }

    public ArrayList<ArrowObject> getArrows(){

        for (List<Integer> j: connections){

            double x1 = nodes.get(j.get(0)).x;
            double y1 = nodes.get(j.get(0)).y;
            double x2 = nodes.get(j.get(1)).x;
            double y2 = nodes.get(j.get(1)).y;

            double arrowX = (x1+x2)/2;
            double arrowY = (y1+y2)/2;
            var point = new Point2D.Double(arrowX,arrowY);
            String class1Name = nodes.get(j.get(0)).name;
            String class2Name = nodes.get(j.get(1)).name;
            arrows.add(new ArrowObject(point, j.get(0), class1Name, j.get(1), class2Name));
        }

        return (arrows);

    }

    public void applyForce() {

        // gravity attracting nodes to center
        for (NodeObject i: nodes){

            i.forceY = -(i.y)*gravityConstant;
            i.forceX = -(i.x)*gravityConstant;
        }

        // mutual repulsion preventing collisions and cluttering
        for (int i=0; i<nNodes; i++) {
            for (int j=i+1; j<nNodes; j++) {

                double vecX = nodes.get(i).x - nodes.get(j).x;
                double vecY = nodes.get(i).y - nodes.get(j).y;
                double vecLengthSquared = vecX*vecX + vecY*vecY;

                nodes.get(i).forceX =  (nodes.get(i).forceX + ((vecX*repulsionConstant)/vecLengthSquared));
                nodes.get(i).forceY =  (nodes.get(i).forceY + ((vecY*repulsionConstant)/vecLengthSquared));
                nodes.get(j).forceX =  (nodes.get(j).forceX - ((vecX*repulsionConstant)/vecLengthSquared));
                nodes.get(j).forceY =  (nodes.get(j).forceY - ((vecY*repulsionConstant)/vecLengthSquared));
            }
        }

        // connected nodes are being pulled together
        for (List<Integer> i: connections){

            NodeObject n1 = nodes.get(i.get(0));
            NodeObject n2 = nodes.get(i.get(1));
            double vecX = n1.x-n2.x;
            double vecY = n1.y-n2.y;
            n1.forceX -= vecX*pullFactor;
            n1.forceY -= vecY*pullFactor;
            n2.forceX += vecX*pullFactor;
            n2.forceY += vecY*pullFactor;
        };

        // apply forces
        for (NodeObject k: nodes){

            k.x += (double) (k.forceX/(k.size*4) );
            k.y += (double) (k.forceY/(k.size*4) );
        }
    }

    public ArrayList<Object> getNearestNode(double mouseX, double mouseY){

        int distMin = 10000000;

        for (int i=0; i<nodes.size(); i++) {

            NodeObject n = nodes.get(i);
            int distX = (int) (n.x - mouseX);
            int distY = (int) (n.y - mouseY);
            int dist = distX*distX + distY*distY;
            if (dist<distMin) {
                closeNode = nodes.get(i);
                distMin = dist;
            }
        }
        ArrayList<Object> retVal = new ArrayList<>();
        retVal.add(closeNode);
        retVal.add(distMin);
        return(retVal);
    }

    public ArrayList<Object> getNearestArrow(double mouseX, double mouseY){

        int distMin = 10000000;

        for (int i=0; i<arrows.size(); i++) {

            ArrowObject a = arrows.get(i);
            int distX = (int) (a.point.x - mouseX);
            int distY = (int) (a.point.y - mouseY);
            int dist = distX*distX + distY*distY;
            if (dist<distMin) {
                closeArrow = arrows.get(i);
                distMin = dist;
            }
        }
        ArrayList<Object> retVal = new ArrayList<>();
        retVal.add(closeArrow);
        retVal.add(distMin);
        return(retVal);
    }

    public String getNearestNodeOrArrow(double mouseX, double mouseY) {

        var node = getNearestNode(mouseX,mouseY);
        var arrow = getNearestArrow(mouseX,mouseY);

        if ((Integer) node.get(1) < (Integer) arrow.get(1)) {
            return("Node");
        }else{
            return("Arrow");
        }

    }




}
