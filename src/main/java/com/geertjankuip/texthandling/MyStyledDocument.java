package main.java.com.geertjankuip.texthandling;

import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;


public class MyStyledDocument {

    public MyStyledDocument() {

    }

    public DefaultStyledDocument getDefaultStyledDocumentFromDB(ArrayList<TokenContainer> tokenData, String className) {

        StringBuilder text = new StringBuilder();

        StringBuilder tokenStringBuilder = new StringBuilder();

        int linePrev = 10;

        int posPrev = -1;
        int lenPrev = -1;
        int requiredWhiteSpace = 0;
        boolean firstLoop = true;

        DefaultStyledDocument doc = new DefaultStyledDocument();

        SimpleAttributeSet black = new SimpleAttributeSet();
        StyleConstants.setFontFamily(black, "Consolas");
        StyleConstants.setFontSize(black, 16);
        StyleConstants.setLineSpacing(black, 1.0F);
        StyleConstants.setForeground(black, new Color(30,30,30));

        SimpleAttributeSet red = new SimpleAttributeSet(black);
        StyleConstants.setBold(red, true);
        StyleConstants.setForeground(red, new Color(220,30,20));


        for (TokenContainer t : tokenData) {

            if (firstLoop) {
                for (int i = 0; i < t.pos; i++) {
                    tokenStringBuilder.append(" ");
                }
                tokenStringBuilder.append(t.representation);
            } else if (t.line > linePrev) {
                for (int i = 0; i < (t.line - linePrev); i++) {
                    tokenStringBuilder.append("\n");
                }
                for (int i = 0; i < t.pos; i++) {
                    tokenStringBuilder.append(" ");
                }
                tokenStringBuilder.append(t.representation);
            } else {
                requiredWhiteSpace = t.pos - posPrev - lenPrev;
                for (int i = 0; i < requiredWhiteSpace; i++) {
                    tokenStringBuilder.append(" ");
                }
                tokenStringBuilder.append(t.representation);
            }

            posPrev = t.pos;
            lenPrev = t.representation.length();
            linePrev = t.line;
            firstLoop = false;

            String tokenAsString = tokenStringBuilder.toString();
            int len = tokenStringBuilder.length();
            tokenStringBuilder.delete(0, len);

            try {
                String trimmed = (t.representation).trim();
                if (trimmed.equals(className)) {
                    doc.insertString(doc.getLength(), tokenAsString, red);
                }else{
                    doc.insertString(doc.getLength(), tokenAsString, black);
                }
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }

        return doc;
    }
}