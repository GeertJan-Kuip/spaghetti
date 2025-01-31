package main.java.com.geertjankuip.swingworkers;

import main.java.com.geertjankuip.gui.Controller;
import main.java.com.geertjankuip.logging.ActivityLogger;
import main.java.com.geertjankuip.sqlite.SQLiteWriter;
import main.java.com.geertjankuip.texthandling.DirectoryReader;
import main.java.com.geertjankuip.texthandling.FileDataContainer;
import main.java.com.geertjankuip.texthandling.TextDataContainer;
import main.java.com.geertjankuip.texthandling.TextHandler;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;


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
