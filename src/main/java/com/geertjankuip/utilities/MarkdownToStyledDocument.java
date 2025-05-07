package com.geertjankuip.utilities;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;


public class MarkdownToStyledDocument {

    public static DefaultStyledDocument readMarkDownFile() {

        File file = new File("README.md");

        DefaultStyledDocument doc = new DefaultStyledDocument();

        SimpleAttributeSet regularFont = new SimpleAttributeSet();
        StyleConstants.setFontFamily(regularFont, "consolas");
        StyleConstants.setFontSize(regularFont, 16);
        StyleConstants.setLineSpacing(regularFont, 1.0F);
        StyleConstants.setForeground(regularFont, new Color(30,30,30));

        SimpleAttributeSet headerFont = new SimpleAttributeSet(regularFont);
        StyleConstants.setFontFamily(headerFont, "consolas");
        StyleConstants.setFontSize(headerFont, 24);
        StyleConstants.setBold(headerFont, true);
        StyleConstants.setForeground(headerFont, new Color(10,10,10));


        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {

                List<Object> modifiedLine = convertLineToStyledDoc(line);

                String newLine = (String) modifiedLine.get(0);
                Integer fontSize = (Integer) modifiedLine.get(1);

                if (fontSize.equals(16)) {
                    doc.insertString(doc.getLength(), newLine, regularFont);
                } else {
                    StyleConstants.setFontSize(headerFont, fontSize);
                    doc.insertString(doc.getLength(), newLine, headerFont);
                }

                lineNumber++;
            }
        } catch (IOException | BadLocationException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }

        return(doc);
    }

    private static List<Object> convertLineToStyledDoc(String line){

        String retLine;
        Integer fontSize;

        if (line!="") {

            if (!line.startsWith("#")) {
                retLine = line;
                fontSize = 16;
            } else if (line.startsWith("###")) {
                retLine = line.substring(3);
                retLine = retLine.trim();
                fontSize = 20;
            } else if (line.startsWith("##")) {
                retLine = line.substring(2);
                retLine = retLine.trim();
                fontSize = 24;
            } else if (line.startsWith("#")) {
                retLine = line.substring(1);
                retLine = retLine.trim();
                fontSize = 28;
            } else {
                retLine = line;
                fontSize = 16;
            }
        } else {
            retLine = line;
            fontSize = 16;
        }


        List<Object> returnValue = Arrays.asList(retLine + "\n", fontSize);

        return(returnValue);
    }
}
