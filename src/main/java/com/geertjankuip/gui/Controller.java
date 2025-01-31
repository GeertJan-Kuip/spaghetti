package com.geertjankuip.gui;

import com.geertjankuip.graphics2dpanel.ArrowObject;
import com.geertjankuip.graphics2dpanel.Graphics2DCalculations;
import com.geertjankuip.graphics2dpanel.Graphics2DPanel;
import com.geertjankuip.graphics2dpanel.NodeObject;
import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.sqlite.SQLiteReader;

import com.geertjankuip.swingworkers.WorkerClassData;
import com.geertjankuip.swingworkers.WorkerGraphics2DPanel;
import com.geertjankuip.swingworkers.WorkerSetJTextPaneFromDB;
import com.geertjankuip.swingworkers.WorkerTokensAndDictionary;
import com.geertjankuip.texthandling.*;
import com.geertjankuip.utilities.MarkdownToStyledDocument;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Controller {

    GUI gui;
    ActivityLogger logger;

    int graphics2DPanelWidth;
    int graphics2DPanelHeight;


    public Controller(String componentName, GUI gui, ActivityLogger logger) throws SQLException {

        this.gui = gui;
        this.logger = logger;

        switch(componentName) {

            case "loadGitItem":
                gui.setJTextPane(MarkdownToStyledDocument.readMarkDownFile(), "Top");
                break;
            case "loadSrcFolderItem":
                deleteDataBase();
                getFilesFoldersTokensAndDictionary();
                break;
            case "settingsItem":
                gui.setJTextPane(MarkdownToStyledDocument.readMarkDownFile(), "TopRight");
                break;
            case "showDBItem":
                SQLiteReader sql2 = new SQLiteReader(logger);
                String res = sql2.getFilesTableHTML();
                this.gui.setJTextPaneHTML(res);
                break;
            case "clearDBItem":
                break;
            case "showGraphItem":
                startGraphicsPanel();
                break;
            case "howItem":
                setJTextPaneFromDB(1, "Top");
                setJTextPaneFromDB(2, "Bottom");
                break;
            case "aboutItem":
                logger.logAction("This is the logger");
                break;
            case "switch_card":
                CardLayout cl = (CardLayout)(gui.myJPanelCardsTopRight.getLayout());
                cl.show(gui.myJPanelCardsTopRight, "cardHelp");
                break;
        }
    }

    private void deleteDataBase() {

        File dbFile = new File("mydb.db");

        if (dbFile.delete()) {
            logger.logAction("Database 'mydb.db' is deleted and will be recreated in the next step");
        } else if (dbFile.exists()){
            logger.logAction("Delete database 'mydb.db' failed. It might be opened by some program");
        } else {
            logger.logAction("Created database 'mydb.db'");
        }
    }

    private void startGraphicsPanel(){
        graphics2DPanelWidth = gui.getWidthUpperRightPanel();
        graphics2DPanelHeight = gui.getHeightUpperRightPanel();

        (new WorkerGraphics2DPanel(logger, this, graphics2DPanelWidth, graphics2DPanelHeight)).execute();
    }

    public void showGraphicsPanel(ArrayList<NodeObject> nodes,Set<List<Integer>> connections,ArrayList<ArrowObject> arrows,Graphics2DCalculations graphics2DCalculations) {

        JPanel myGraphicsPanel = new Graphics2DPanel(nodes, connections, arrows, graphics2DCalculations, graphics2DPanelWidth, graphics2DPanelHeight, this);
        gui.setGraphicsPanel(myGraphicsPanel);
    }

    private boolean getFilesFoldersTokensAndDictionary() {

        DirectoryReader dR = new DirectoryReader(logger);

        if (dR.getValidFolderAndStartProcessingIt()) {

            (new WorkerTokensAndDictionary(logger, dR, this)).execute();
            return true;

        }else{
            return false;
        }
    }

    public void getClassDataIncludingRelations() {

        (new WorkerClassData(logger)).execute();

    }

    public void setJTextPaneFromDB(int classID, String whichPanel){

        (new WorkerSetJTextPaneFromDB(gui, logger, classID, whichPanel)).execute();
    }
}