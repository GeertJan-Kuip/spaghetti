package com.geertjankuip.texthandling;

import com.geertjankuip.logging.ActivityLogger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFileReader {

    String path;

    ActivityLogger logger;

    private ArrayList<ArrayList<Integer>> allPositions = new ArrayList<ArrayList<Integer>>();
    private ArrayList<String> allLineStrings = new ArrayList<>();
    private ArrayList<Integer> positions = new ArrayList<Integer>();

    // simpleOperator  - (),.;[]{} non-ambiguous, always one character
    final private List<Integer> simpleOperator = Arrays.asList(40, 41, 44, 46, 59, 91, 93, 123, 125);
    // complexOperator: !%&*+-/:<=>^|    -  these operators need next character before they can be interpretated
    final private List<Integer> complexOperator = Arrays.asList(33, 37, 38, 42, 43, 45, 47, 58, 60, 61, 62, 94, 124);
    // wordStart: A-Z,a-z,1-9,_,$
    final private List<Integer> wordsAndNumbers = Arrays.asList(36, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122);


    public TextFileReader(String path, ActivityLogger logger) {

        this.logger = logger;
        this.path = path;

        readTextFile();
    }

    public ArrayList<String> getAllLineStrings(){

        return(allLineStrings);
    }

    public ArrayList<ArrayList<Integer>> getAllPositions(){

        return(allPositions);
    }

    public TextDataContainer getAsTextDataContainer() {

        return(new TextDataContainer(allLineStrings, allPositions, path));
    }



    private void readTextFile() {

        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                unicodeLineReader(line.stripTrailing());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unicodeLineReader(String line) throws InterruptedException {

        short mode = 0;
        short modeT = 0;
        byte[] bytes = line.getBytes();
        int c = 0;
        int cPrev = 0;
        int cPrevPrev = 0;

        for (int i = 0; i < bytes.length; i++) {

            String cStr = line.substring(i,i+1);
            c = bytes[i];
            mode = modeT;
            modeT = -1;
            boolean EOL = (i+1)==bytes.length;
            if (i > 0) {
                cPrev = bytes[i - 1];
            }
            if (i>1) {
                cPrevPrev = bytes[i - 2];
            }

            // dealing with triple quotes
            if (isTripleSingleQuote(cPrevPrev,cPrev,c) && mode==7) {
                modeT=0;
                put(i+1);
            }else if (isTripleSingleQuote(cPrevPrev,cPrev,c) && mode!=7 && mode!=5 && mode!=6) {
                modeT=7;
                moveUp(i+1);
            }else if (isTripleDoubleQuote(cPrevPrev,cPrev,c) && mode==8) {
                modeT=0;
                put(i+1);
            }else if (isTripleDoubleQuote(cPrevPrev,cPrev,c) && mode!=8 && mode!=5 && mode!=6) {
                modeT=0;
                moveUp(i+1);
            }

            // comments, end of line
            else if (EOL && mode==5) {   // hdsfakhgfa
                put(i+1);
                modeT=0;
            }else if (EOL && mode==6 && isEndMultiLineComment(cPrev,c)) {   //  sdaf */
                put(i+1);
                modeT=0;
            }else if (EOL && mode==6){   //  /* ijdf098ay=
                put(i+1);
                modeT=6;

            // end of line
            }else if (EOL && isSimpleOperator(c) && isProceedWord(cPrev)){  // myAction;
                put(i);
                put(i);
                put(i+1);
                modeT=0;
            }else if (EOL && isSimpleOperator(c) && isComplexOperator(cPrev,c)){  //  index++;
                put(i);
                put(i);
                put(i+1);
                modeT=0;
            }else if (EOL && isSimpleOperator(c) && mode==2){   //  .*;
                put(i);
                put(i);
                put(i+1);
                modeT=0;
            }else if (EOL && isSimpleOperator(c)){   // }  ,  ();
                put(i);
                put(i+1);
                modeT=0;
            }else if (EOL && isProceedWord(cPrev) && !isSimpleOperator(c)){   // should not happen
                put(i);
                modeT=0;
            }else if (EOL) {
                if ((positions.size()) % 2 == 0) {
                    put(i);
                }
                put(i + 1);
                modeT = 0;
            }
            // mode 0 - neutral mode, start of new line or previous token has been resolved
            else if (mode==0 && isWhitespace(c)){
                modeT=0;
            }else if (mode==0 && isStartWord(c)){
                put(i);
                modeT=1;
            }else if (mode==0 && isSimpleOperator(c)){
                put(i);
                put(i+1);
                modeT=0;
            }else if (mode==0 && isStartComplexOperator(c)){
                put(i);
                modeT=2;
            }else if (mode==0 && isSingleQuote(c)){
                put(i);
                modeT=3;
            }else if (mode==0 && isDoubleQuote(c)){
                put(i);
                modeT=4;
            }else if (mode==0){
                modeT=0;
            }
            // mode 1 - cursor is within a word
            else if (mode==1 && isWhitespace(c)) {
                put(i);
                modeT = 0;
            } else if (mode==1 && isProceedWord(c)) {
                modeT = 1;
            } else if (mode==1 && isSimpleOperator(c)) {
                put(i);
                put(i);
                put(i + 1);
                modeT = 0;
            } else if (mode==1 && isStartComplexOperator(c)) {
                put(i);
                put(i);
                modeT=2;
            } else if (mode==1) {
                modeT = 0;
            }
            // mode 2 - previous character was complex operator !%&*+-/:<=>^|
            else if (mode==2 && isWhitespace(c)) {
                put(i);
                modeT = 0;
            } else if (mode==2 && isStartWord(c)) {
                put(i);
                put(i);
                modeT = 1;
            } else if (mode==2 && isSimpleOperator(c)) {
                put(i);
                put(i);
                put(i + 1);
                modeT = 0;
            } else if (mode==2 && isSingleQuote(c)) {
                put(i);
                put(i);
                modeT = 3;
            } else if (mode==2 && isDoubleQuote(c)) {
                put(i);
                put(i);
                modeT = 4;
            } else if (mode==2 && isStartOneLineComment(cPrev, c)) {
                modeT = 5;
            } else if (mode==2 && isStartMultiLineComment(cPrev, c)) {
                modeT = 6;
            } else if (mode==2 && isComplexOperator(cPrev, c)) {
                put(i + 1);
                modeT = 0;
            } else if (mode==2) {
                modeT = 0;
            }

            // mode 3 - cursor is within a string marked by single quotes
            else if (mode==3 && isSingleQuoteEnd(cPrev, c)) {
                put(i+1);
                modeT = 0;
            } else if (mode==3){
                modeT = 3;
            }
            // mode 4 - cursor is within a string marked by double quotes
            else if (mode==4 && isDoubleQuoteEnd(cPrev, c)) {
                put(i+1);
                modeT = 0;
            }else if (mode==4){
                modeT = 4;
            }

            // mode 5 - cursor is within a '//' comment line
            else if (mode==5){
                modeT=5;
            }
            // mode 6 - cursor is within a '/*' comment line
            else if (mode==6 && isEndMultiLineComment(cPrev,c)){
                put(i+1);
                modeT=0;
            } else if (mode==6 && i==0){
                put(i);
                modeT=6;
            } else if (mode==6){
                modeT=6;
            }
            // mode 7 - cursore is within a ''' (multiline) string
            else if (mode==7 && i==0){
                put(i);
                modeT=7;
            }else if (mode==7){
                modeT=7;
            }
            // mode 7 - cursor is within a  """ (multiline) string
            else if (mode==8 && i==0){
                put(i);
                modeT=8;
            }else if (mode==8){
                modeT=8;
            }
            // if none of the statements applied, send a message and store the whole line
            else{

            }

        } // end if statements

        // check if positions array is valid. It must have an even number of items, order must be ascending and highest number max bytes.length+1
        if (!(isValidPositionsArray(bytes.length))) {
            positions.clear();
            positions.add(0);
            positions.add(bytes.length);

            SwingUtilities.invokeLater(new Runnable() {
                public void run(){

                    logger.logAction("Problem in: " + path + "\nLine " + line + " couldn't be parsed " + "\nIt has been stored as a full line.");
                }
            });
        }

        ArrayList<Integer> posCopy = (ArrayList<Integer>) positions.clone();
        allPositions.add(posCopy);
        allLineStrings.add(line);

        positions.clear();
    }


    private void put(int pos) {
        positions.add(pos);
    }

    private void moveUp(int pos) {
        positions.set(positions.size()-1, pos);
    }

    private void remove(int pos) {
        positions.remove(positions.size() -1);
    }

    private boolean isValidPositionsArray(int lineLength) {
        boolean isEven = ((positions.size())%2 == 0);

        boolean isAscending = true;
        int pPrev = 0;
        for (int p: positions){
            if (p<pPrev){
                isAscending = false;
            }
            pPrev = p;
        }

        boolean isWithinLine = true;
        if (positions.size()>1) {
            isWithinLine = (positions.get(positions.size()-1) <= lineLength);
        }


        if (!isEven || !isAscending || !isWithinLine) {
            return false;
        }else {
            return true;
        }
    }

    private boolean isWhitespace(int c) {

        return (c == 32);
    }
    private boolean isStartWord(int c) {

        return (wordsAndNumbers.contains(c));
    }
    private boolean isProceedWord(int c) {

        return (wordsAndNumbers.contains(c));
    }
    private boolean isSimpleOperator(int c) {

        return (simpleOperator.contains(c));
    }
    private boolean isSingleQuote(int c) {

        return (c == 39);
    }
    private boolean isSingleQuoteEnd(int cPrev, int c) {

        return (cPrev!=92 && c==39);
    }
    private boolean isDoubleQuote(int c) {

        return (c == 34);
    }
    private boolean isDoubleQuoteEnd(int cPrev, int c) {

        return (cPrev!=92 && c==34);
    }
    private boolean isStartComplexOperator(int c) {

        return (complexOperator.contains(c));
    }
    private boolean isComplexOperator(int cPrev, int c) {

        return (complexOperator.contains(cPrev) && complexOperator.contains(c));
    }
    private boolean isStartOneLineComment(int cPrev, int c) {

        return (cPrev==47 && c==47);
    }
    private boolean isStartMultiLineComment(int cPrev, int c) {

        return (cPrev==47 && c==42);
    }
    private boolean isEndMultiLineComment(int cPrev, int c) {

        return (cPrev==42 && c==47);
    }
    private boolean isTripleSingleQuote(int cPrevPrev, int cPrev, int c) {

        return (cPrevPrev==39 && cPrev==39 && c==39);
    }
    private boolean isTripleDoubleQuote(int cPrevPrev, int cPrev, int c) {

        return (cPrevPrev==34 && cPrev==34 && c==34);
    }

}
