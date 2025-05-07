package com.geertjankuip.swingworkers;

import com.geertjankuip.gui.GUI;
import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.sqlite.SQLiteReader;
import com.geertjankuip.texthandling.*;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.sql.SQLException;
import java.util.ArrayList;


public class WorkerSetJTextPaneFromDB extends SwingWorker<String, String> {

    GUI gui;
    ActivityLogger logger;
    int classID1;
    int classID2;
    String whichPanel;

    public WorkerSetJTextPaneFromDB(GUI gui, ActivityLogger log, int classID1, int classID2, String whichPanel){

        this.gui = gui;
        this.logger = logger;
        this.classID1 = classID1;
        this.classID2 = classID2;
        this.whichPanel = whichPanel;
    }

    @Override
    protected String doInBackground() throws Exception {

        SQLiteReader sql2 = new SQLiteReader(logger);

        ArrayList<TokenContainer> tokens = null;
        String className1 = null;
        String className2 = null;


        try {
            tokens = sql2.getFileAsTokens(classID1);
            className1 = sql2.getClassName(classID1);
            className2 = sql2.getClassName(classID2);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (tokens!=null) {
            MyStyledDocument myStyledDocument = new MyStyledDocument();

            DefaultStyledDocument doc = myStyledDocument.getDefaultStyledDocumentFromDB(tokens, className1, className2, whichPanel);
            gui.setJTextPane(doc, whichPanel);
        }

        return "";
    }
}
