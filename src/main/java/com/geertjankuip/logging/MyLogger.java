package com.geertjankuip.logging;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;


public class MyLogger implements ActivityLogger {

    DefaultStyledDocument doc;

    SimpleAttributeSet black = new SimpleAttributeSet();

    JTextPane target;
    StringBuilder totalLog = new StringBuilder("");

    public MyLogger(JTextPane target){

        this.target=target;
        this.doc = (DefaultStyledDocument) target.getStyledDocument();
    }

    @Override
    public void logAction(String message) {

        try {
            updateStyledDocument(message + "\n");
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        target.repaint();
    }

    public void updateStyledDocument(String message) throws BadLocationException {

        StyleConstants.setFontFamily(black, "Consolas");
        StyleConstants.setFontSize(black, 16);
        StyleConstants.setLineSpacing(black, 1.0F);
        StyleConstants.setForeground(black, new Color(30,30,30));

        doc.insertString(doc.getLength(), message, black);
    }
}
