package com.geertjankuip.sqlite;

import com.geertjankuip.logging.ActivityLogger;
import com.geertjankuip.logging.MyLogger;
import com.geertjankuip.texthandling.FileDataContainer;
import com.geertjankuip.texthandling.TextDataContainer;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteWriter {

    private Connection connection;
    private ActivityLogger logger;

    public SQLiteWriter(ActivityLogger logger)  {

        this.logger = logger;

        connectDB();

        try {
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectDB() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run(){
                    logger.logAction("PROBLEM: cannot connect to SQLite database");
                }
            });
            e.getMessage();
        }
    }

    private void closeDB() {

        try{
            connection.close();
        } catch (SQLException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run(){
                    logger.logAction("PROBLEM: cannot close SQLite database");
                }
            });
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws SQLException {

        String str1 = "CREATE TABLE IF NOT EXISTS tokens (" +
                " id INTEGER PRIMARY KEY, " +
                " token INTEGER," +
                " place INTEGER NOT NULL," +
                " line INTEGER NOT NULL," +
                " file INTEGER" +
                " );";

        String str2 = "CREATE TABLE IF NOT EXISTS dictionary (" +
                " id INTEGER PRIMARY KEY, " +
                " representation TEXT UNIQUE, " +
                " color INTEGER " +
                " );";

        String str3 = "CREATE TABLE IF NOT EXISTS files (" +
                " id INTEGER NOT NULL, " +
                " isFile BOOLEAN NOT NULL, " +
                " name TEXT NOT NULL, " +
                " path TEXT NOT NULL, " +
                " parent INTEGER NOT NULL" +
                " );";

        String str4 = "CREATE TABLE IF NOT EXISTS classes (" +
                " id INTEGER PRIMARY KEY, " +
                " token_id INTEGER, " +
                " token_id_end INTEGER, " +
                " dictionary_id INTEGER, " +
                " place INTEGER, " +
                " line INTEGER, " +
                " line_end INTEGER, " +
                " file INTEGER, " +
                " representation TEXT, " +
                " is_innerclass BOOLEAN);";

        String str5 = "CREATE TABLE IF NOT EXISTS classrelations (" +
                "class1 INTEGER, " +
                "class2 INTEGER, " +
                "token_id INTEGER); ";


        Statement stmnt1 = connection.createStatement();
        stmnt1.execute(str1);
        Statement stmnt2 = connection.createStatement();
        stmnt2.execute(str2);
        Statement stmnt3 = connection.createStatement();
        stmnt3.execute(str3);
        Statement stmnt4 = connection.createStatement();
        stmnt4.execute(str4);
        Statement stmnt5 = connection.createStatement();
        stmnt5.execute(str5);
        connection.commit();

        stmnt1.close();
        stmnt2.close();
        stmnt3.close();
        stmnt4.close();
        connection.close();

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction("Five database tables created");
            }
        });

    }

    public HashMap<String, Integer> getFilePathIndexes() throws SQLException {

        HashMap<String, Integer> retVal = new HashMap<>();
        String query = "SELECT id, path FROM files;";


        Statement stmnt = connection.createStatement();
        ResultSet res = stmnt.executeQuery(query);
        connection.commit();

        while (res.next()) {
            retVal.put(res.getString("path"), res.getInt("id"));
        }

        return retVal;
    }

    public void writeClassRelationsTable(ArrayList<Integer> classRelations) throws SQLException {

        connectDB();

        for (int i=0; i<classRelations.size(); i=i+3){

            String query1 = """
                    INSERT INTO classrelations (class1, class2, token_id) VALUES (?,?,?);                        
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setInt(1, classRelations.get(i));
            preparedStatement.setInt(2, classRelations.get(i+1));
            preparedStatement.setInt(3, classRelations.get(i+2));
            preparedStatement.execute();
            preparedStatement.close();
        }
        connection.commit();


        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction(classRelations.size() + " relationships between classes have been identified");
            }
        });

        closeDB();
    }

    public void writeClassTable(ArrayList<ClassContainer> classList) throws SQLException {

        connectDB();

        for (ClassContainer c: classList ) {

            String query1 = """
                INSERT INTO classes(token_id, token_id_end, dictionary_id, place, line, line_end, file, representation, is_innerclass) 
                VALUES (?,?,?,?,?,?,?,?,?);
                """;

            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setInt(1, c.token_id);
            preparedStatement.setInt(2, c.token_id_end);
            preparedStatement.setInt(3, c.token_dict_ref);
            preparedStatement.setInt(4, c.place);
            preparedStatement.setInt(5, c.line);
            preparedStatement.setInt(6, c.lineend);
            preparedStatement.setInt(7, c.file);
            preparedStatement.setString(8, c.representation);
            preparedStatement.setBoolean(9, c.isinnerclass);

            preparedStatement.execute();
            preparedStatement.close();

        }

        connection.commit();

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction(classList.size() + " classes written to table");
            }
        });

        closeDB();
    }

    public void writeDictionaryTokensTables(ArrayList<TextDataContainer> allTextData) throws SQLException {

        ArrayList<TokenInsert> allTokens = new ArrayList<>();

        ArrayList<String> allLines;
        ArrayList<ArrayList<Integer>> allPositions;
        String filePath;
        Integer fileIndex;

        connectDB();
        HashMap<String, Integer> pathLookUpID = getFilePathIndexes();

        for (TextDataContainer k: allTextData) {

            allLines = k.getLines();
            allPositions = k.getPositions();
            filePath = k.getPath();
            fileIndex = pathLookUpID.get(filePath);



            for (int j = 0; j < allPositions.size(); j++) {

                ArrayList<Integer> positions = allPositions.get(j);
                String line = allLines.get(j);

                for (int i = 0; i < positions.size(); i = i + 2) {

                    int pos1 = positions.get(i);
                    int pos2 = positions.get(i + 1);
                    int lineNumber = j;
                    String token = line.substring(pos1, pos2);
                    TokenInsert tokenInsert = new TokenInsert(token, pos1, lineNumber, fileIndex);

                    allTokens.add(tokenInsert);
                }
            }
        }






        for (TokenInsert i : allTokens) {

            String query1 = "INSERT OR IGNORE INTO dictionary(representation, color) VALUES (?,?); ";

            var pstmt = connection.prepareStatement(query1);
            pstmt.setString(1, i.token);
            pstmt.setInt(2, 0);

            pstmt.execute();
        }
        connection.commit();

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction("Dictionary table inserts completed");
            }
        });

        int counter = 0;
        for (TokenInsert i : allTokens) {
            String query2 = "INSERT INTO tokens(token, place, line, file) " +
                    " values ((SELECT id FROM dictionary WHERE dictionary.representation = ?), ?,?,?); ";

            var pstmt = connection.prepareStatement(query2);
            pstmt.setString(1, i.token);
            pstmt.setInt(2, i.place);
            pstmt.setInt(3, i.line);
            pstmt.setInt(4, i.file);

            pstmt.execute();
            counter++;
        }
        connection.commit();

        final int counter2 = counter;

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction( counter2 + " tokens inserted in tokens table" );
            }
        });

        closeDB();
    }

    public class TokenInsert {

        String token;
        int place;
        int line;
        int file;

        public TokenInsert(String token, int place, int line, int file) {
            this.token = token;
            this.place = place;
            this.line = line;
            this.file = file;
        }
    }

    public void writeFilesTable(List<FileDataContainer> arrFiles) throws SQLException {

        connectDB();

        String query = "INSERT INTO files(id, isFile, name, path, parent) VALUES (?,?,?,?,?) ";

        var pstmt = connection.prepareStatement(query);

        for (FileDataContainer fdc : arrFiles) {

            pstmt.setInt(1, fdc.getID());
            pstmt.setBoolean(2, fdc.getIsFile());
            pstmt.setString(3, fdc.getName());
            pstmt.setString(4, fdc.getFileObject().getPath());
            pstmt.setInt(5, fdc.getParent());
            pstmt.execute();
        }

        connection.commit();



        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction("Files table inserts completed");
            }
        });

        closeDB();
    }
}
