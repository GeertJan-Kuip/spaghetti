package main.java.com.geertjankuip.swingworkers;

import main.java.com.geertjankuip.logging.ActivityLogger;
import main.java.com.geertjankuip.sqlite.ClassContainer;
import main.java.com.geertjankuip.sqlite.SQLiteReader;
import main.java.com.geertjankuip.sqlite.SQLiteWriter;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;


public class WorkerClassData extends SwingWorker<String, String>{

    ActivityLogger logger;

    public WorkerClassData(ActivityLogger logger) {

        this.logger = logger;
    }

    @Override
    protected String doInBackground() throws Exception {

        SQLiteReader sqlReader = new SQLiteReader(logger);

        try {
            sqlReader.getClasses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            sqlReader.getClassScopes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ArrayList<ClassContainer> classList = sqlReader.getClassList();

        SQLiteWriter sqlWriter = new SQLiteWriter(logger);

        try {
            sqlWriter.writeClassTable(classList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            sqlReader.getClassRelations();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Integer> classRelations = sqlReader.getClassRelationsData();

        try {
            sqlWriter.writeClassRelationsTable(classRelations);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    protected void done(){

        logger.logAction("Load process completed");
    }

}
