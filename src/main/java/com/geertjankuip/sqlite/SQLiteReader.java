package main.java.com.geertjankuip.sqlite;

import main.java.com.geertjankuip.graphics2dpanel.ClassContainerGraphics;
import main.java.com.geertjankuip.logging.ActivityLogger;
import main.java.com.geertjankuip.texthandling.TokenContainer;

import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SQLiteReader {

    private Connection connection;

    private ArrayList<ClassContainer> classList = new ArrayList<>();

    private ArrayList<Integer> classRelationsData = new ArrayList<>();

    private ActivityLogger logger;

    public SQLiteReader(ActivityLogger logger){

        this.logger = logger;
    }


    public ArrayList<TokenContainer> getFileAsTokens(int classID) throws SQLException {

    var returnArray = new ArrayList<TokenContainer>();

    connectDB();

    String query = """
                SELECT tokens.*, dictionary.representation, dictionary.color, classes.id 
                FROM tokens 
                INNER JOIN dictionary ON tokens.token = dictionary.id 
                INNER JOIN classes ON tokens.file=classes.file 
                WHERE classes.id = """ + classID;

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);
    connection.commit();

    while (resultSet.next()){

        int line = resultSet.getInt("line");
        int pos = resultSet.getInt("place");
        int color = resultSet.getInt("color");
        String representation = resultSet.getString("representation");

        TokenContainer tokenContainer = new TokenContainer(line,pos,color,representation);
        returnArray.add(tokenContainer);
    }

    statement.close();
    resultSet.close();
    closeDB();

    return(returnArray);
}

    public String getClassName(int id) throws SQLException {

        connectDB();

        String returnedClassName = null;

        String sql = "SELECT classes.representation FROM classes WHERE classes.id=" + id;

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {

            returnedClassName = resultSet.getString("representation");
        }

        statement.close();
        resultSet.close();
        closeDB();

        return(returnedClassName);
    }

    public ArrayList<ClassContainer> getClassList() {
        return(classList);
    }

    public ArrayList<Integer> getClassRelationsData() {
        return(classRelationsData);
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
            throw new RuntimeException(e);
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

    public String getFilesTableHTML() throws SQLException {

        connectDB();

        String str = null;

        String query = "SELECT * FROM files";
        Statement stmnt = connection.createStatement();
        ResultSet res = stmnt.executeQuery(query);
        connection.commit();

        StringBuilder strb = new StringBuilder();
        strb.append("<table><tr><td>id</td><td>isFile</td><td>name</td><td>path</td><td>parent</td></tr>");

        while (res.next()) {
            strb.append("<tr>");
            strb.append("<td>" + res.getInt("id") + "</td>");
            strb.append("<td>" + res.getBoolean("isFile") + "</td>");
            strb.append("<td>" + res.getString("name") + "</td>");
            strb.append("<td>" + res.getString("path") + "</td>");
            strb.append("<td>" + res.getInt("parent") + "</td>");
            strb.append("</tr>");
        }
        strb.append("</table>");
        str = strb.toString();

        closeDB();
        return str;
    }

    public void getClassRelations() throws SQLException {

        connectDB();

        for (ClassContainer c : classList) {

            String query1 =  "SELECT classes.id FROM classes WHERE classes.representation = ? ;" ;

            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, c.representation);
            ResultSet resultSet1 = preparedStatement1.executeQuery();

            connection.commit();

            while (resultSet1.next()) {

                c.id = resultSet1.getInt("id");
            }

            preparedStatement1.close();

            String query2 = """
                    SELECT classes.representation, classes.id AS classid, classes.file, tokens.line, tokens.id AS tokensid FROM classes 
                    INNER JOIN tokens ON tokens.id BETWEEN classes.token_id AND classes.token_id_end 
                    WHERE tokens.token = ? 
                    AND 
                    tokens.id != ? 
                    AND classes.representation != ? 
                    ;                
                    """;

            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setInt(1, c.token_dict_ref);
            preparedStatement2.setInt(2, c.token_id);
            preparedStatement2.setString(3, c.representation);

            ResultSet resultSet2 = preparedStatement2.executeQuery();
            connection.commit();

            while (resultSet2.next()) {

                classRelationsData.add(c.id);
                classRelationsData.add(resultSet2.getInt("classid"));
                classRelationsData.add(resultSet2.getInt("tokensid"));
            }

            preparedStatement2.close();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction("Class relations written to table");
            }
        });

        closeDB();
    }

    public void getClassScopes() throws SQLException {

        connectDB();

        for (ClassContainer c : classList) {

            String query1 = """
                    SELECT tokens.id, token, place, line, file, representation FROM tokens INNER JOIN 
                    dictionary ON tokens.token=dictionary.id
                    WHERE token IN
                        (
                        SELECT dictionary.id FROM dictionary WHERE representation in ('{', '}')
                        )
                    AND file = ? 
                    AND line >= ? ;
                    """ ;

            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setInt(1, c.file);
            preparedStatement.setInt(2, c.line);
            ResultSet resultSet1 = preparedStatement.executeQuery();

            connection.commit();

            // Find the closing bracket of the class
            int bracketOpen = 0;
            int bracketClose = 0;
            while (resultSet1.next()) {

                String bracket = resultSet1.getString("representation");
                if (bracket.equals("{")) {
                    bracketOpen++;
                } else {
                    bracketClose++;
                }

                if (bracketOpen==bracketClose) {
                    c.lineend = resultSet1.getInt("line");
                    c.token_id_end = resultSet1.getInt("id");
                    break;
                }
            }

            resultSet1.close();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction("Inserted class start- and endpoints");
            }
        });


        HashSet unique = new HashSet();
        Set<ClassContainer> classesInFileWithMultipleClasses = classList.stream().
                filter(n->!unique.add(n.file)).collect(Collectors.toSet());

        for (ClassContainer cc: classesInFileWithMultipleClasses) {
            for (ClassContainer dd : classList) {
                if (cc.file==dd.file && cc.line<dd.line && cc.lineend>dd.lineend) {
                    dd.isinnerclass = true;
                } else if (cc.file==dd.file && cc.line>dd.line && cc.lineend<dd.lineend) {
                    cc.isinnerclass = true;
                }
            }
        }

        closeDB();
    }

    public void getClasses() throws SQLException {

        connectDB();

        String query1 = """ 
                SELECT id, token, place, line, file, representation FROM 
                (
                    SELECT *, 
                    LAG(token, 1, 0) OVER (PARTITION BY tokens.file ORDER BY tokens.id) AS prevToken, 
                    LAG(dictionary.representation,1,"-") OVER (PARTITION BY tokens.file ORDER BY tokens.id) AS prevRepresentation 
                    FROM tokens INNER JOIN dictionary ON tokens.token = dictionary.id 
                    WHERE (file, line) IN 
                        (
                        SELECT file, line FROM tokens WHERE token=
                            (
                            SELECT id FROM DICTIONARY WHERE representation='class'
                            )
                        )
                )
                WHERE prevRepresentation = 'class' 
                """;

        Statement statement1 = connection.createStatement();
        ResultSet resultSet1 = statement1.executeQuery(query1);
        connection.commit();

        while (resultSet1.next()) {

            int id = -1;
            int token_id = resultSet1.getInt("id");
            int token_id_end = 0;
            int token_dict_ref = resultSet1.getInt("token");
            int place = resultSet1.getInt("place");
            int line = resultSet1.getInt("line");
            int lineend = -1;
            int file = resultSet1.getInt("file");
            boolean isinnerclass = false;
            String representation = resultSet1.getString("representation");

            ClassContainer classContainer = new ClassContainer(id, token_id,token_id_end, token_dict_ref,place,line,lineend,file,representation,isinnerclass);
            classList.add(classContainer);
        }

        resultSet1.close();

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                logger.logAction(classList.size() + " classes found");
            }
        });

        closeDB();
    }

    public ArrayList<ClassContainerGraphics> getClassDataForGraphics() throws SQLException {

        connectDB();

        ArrayList<ClassContainerGraphics> returnArray = new ArrayList<>();

        String query1 = """
            SELECT classes.id, classes.representation, classes.is_innerclass, MAX(tokens.line) as maxlines 
            FROM classes INNER JOIN tokens ON classes.file=tokens.file 
            GROUP BY classes.id;            
            """;

        Statement stmnt = connection.createStatement();
        ResultSet res = stmnt.executeQuery(query1);
        connection.commit();

        while (res.next()) {

            int idOneBased = res.getInt("id");
            boolean b = res.getBoolean("is_innerclass");
            String c = res.getString("representation");
            int d = res.getInt("maxlines");

            ClassContainerGraphics cc = new ClassContainerGraphics(idOneBased-1, b, c, d);
            returnArray.add(cc);
        }

        res.close();
        closeDB();

        return(returnArray);
    }

    public Set<List<Integer>> getClassRelationsDataForGraphics() throws SQLException {

        connectDB();

        Set<List<Integer>> returnSet = new HashSet<>();

        String query1 = "SELECT class1, class2 FROM classrelations; ";

        Statement stmnt = connection.createStatement();
        ResultSet res = stmnt.executeQuery(query1);
        connection.commit();

        while (res.next()) {

            int classOneIdOneBased = res.getInt("class1");
            int classTwoIdOneBased = res.getInt("class2");

            List<Integer> cr = Arrays.asList(classOneIdOneBased-1,classTwoIdOneBased-1);
            returnSet.add(cr);
        }
        res.close();
        closeDB();

        return(returnSet);
    }
}

