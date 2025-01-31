package main.java.com.geertjankuip.swingworkers;

import main.java.com.geertjankuip.graphics2dpanel.ArrowObject;
import main.java.com.geertjankuip.graphics2dpanel.Graphics2DCalculations;
import main.java.com.geertjankuip.graphics2dpanel.NodeObject;
import main.java.com.geertjankuip.gui.Controller;
import main.java.com.geertjankuip.logging.ActivityLogger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkerGraphics2DPanel extends SwingWorker<String,String> {

    ActivityLogger logger;
    Controller controller;
    int graphics2DPanelWidth;
    int graphics2DPanelHeight;

    ArrayList<NodeObject> nodes;
    Set<List<Integer>> connections;
    ArrayList<ArrowObject> arrows;

    Graphics2DCalculations graphics2DCalculations;


    public WorkerGraphics2DPanel(ActivityLogger logger, Controller controller, int graphics2DPanelWidth, int graphics2DPanelHeight){

        this.logger = logger;
        this.controller = controller;
        this.graphics2DPanelWidth = graphics2DPanelWidth;
        this.graphics2DPanelHeight = graphics2DPanelHeight;
    }

    @Override
    protected String doInBackground() throws Exception {

        graphics2DCalculations = new Graphics2DCalculations(logger);
        nodes = graphics2DCalculations.getNodesFromDB(graphics2DPanelWidth, graphics2DPanelHeight);
        connections = graphics2DCalculations.getConnectionsDataFromDB();
        for (int i = 0; i < 5000; i++) {

            graphics2DCalculations.applyForce();
        }

        arrows = graphics2DCalculations.getArrows();
        return "";
    }

    @Override
    protected void done() {
        controller.showGraphicsPanel(nodes, connections, arrows,graphics2DCalculations);
    }
}
