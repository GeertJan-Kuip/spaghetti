package com.geertjankuip.swingworkers;

import com.geertjankuip.gui.Controller;
import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.sqlite.SQLiteWriter;
import com.geertjankuip.texthandling.DirectoryReader;
import com.geertjankuip.texthandling.FileDataContainer;
import com.geertjankuip.texthandling.TextDataContainer;
import com.geertjankuip.texthandling.TextHandler;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class WorkerTokensAndDictionary extends SwingWorker<String, String> {

    ActivityLogger logger;
    DirectoryReader directoryReader;
    Controller controller;

    public WorkerTokensAndDictionary(ActivityLogger logger, DirectoryReader directoryReader, Controller controller){

        this.logger = logger;
        this.directoryReader = directoryReader;
        this.controller = controller;
    }


    @Override
    protected String doInBackground() throws Exception {

        ArrayList<FileDataContainer> files = directoryReader.getFileArray();

        SQLiteWriter sqlWriter = new SQLiteWriter(logger);

        try {
            sqlWriter.writeFilesTable(files);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        TextHandler textHandler = new TextHandler(files, logger);

        ArrayList<TextDataContainer> allTextPositionsFilenames = textHandler.getAllData();

        try {
            sqlWriter.writeDictionaryTokensTables(allTextPositionsFilenames);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    protected void done(){

        controller.getClassDataIncludingRelations();
    }

}
