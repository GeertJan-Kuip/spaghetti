package com.geertjankuip.swingworkers;

import com.geertjankuip.gui.GUI;
import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.sqlite.SQLiteReader;
import com.geertjankuip.sqlite.SQLiteWriter;
import com.geertjankuip.texthandling.*;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkerSetJTextPaneFromDB extends SwingWorker<String, String> {

    GUI gui;
    ActivityLogger logger;
    int classID;
    String whichPanel;

    public WorkerSetJTextPaneFromDB(GUI gui, ActivityLogger log, int classID, String whichPanel){

        this.gui = gui;
        this.logger = logger;
        this.classID = classID;
        this.whichPanel = whichPanel;
    }

    @Override
    protected String doInBackground() throws Exception {

        SQLiteReader sql2 = new SQLiteReader(logger);

        ArrayList<TokenContainer> tokens = null;
        String className = null;

        try {
            tokens = sql2.getFileAsTokens(classID);
            className = sql2.getClassName(classID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (tokens!=null) {
            MyStyledDocument myStyledDocument = new MyStyledDocument();

            DefaultStyledDocument doc = myStyledDocument.getDefaultStyledDocumentFromDB(tokens, className);
            gui.setJTextPane(doc, whichPanel);
        }

        return "";
    }


}
