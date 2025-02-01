package main.java.com.geertjankuip.gui;

import main.java.com.geertjankuip.logging.MyLogger;
import main.java.com.geertjankuip.utilities.MarkdownToStyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;


public class GUI implements ActionListener{

    private JFrame myFrame;
    private JMenuBar myMenuBar;
    private JMenu fileMenu;
    private JMenu viewMenu;
    private JMenu graphMenu;
    private JMenu dbMenu;
    private JMenu helpMenu;
    private JMenuItem loadSrcFolderItem;
    private JMenuItem settingsItem;
    private JMenuItem showDBItem;
    private JMenuItem clearDBItem;
    private JMenuItem showGraphItem;
    private JMenuItem howItem;
    private JMenuItem aboutItem;

    private JPanel myTopStrip;
    private JButton myButton01;
    private JButton myButton02;
    private JButton myButton03;
    private JButton myButton04;

    private JSplitPane mySplitPaneMain;
    private JSplitPane mySplitPaneLeft;
    private JSplitPane mySplitPaneRight;
    private JPanel myBasePaneBottomRight;
    private JPanel myBasePaneTopRight;
    private JPanel myBasePaneTopLeft;
    private JPanel myBasePaneBottomLeft;
    private JPanel myButtonPane1;
    private JPanel myButtonPane2;
    private JPanel myButtonPane3;

    private JScrollPane myJScrollPaneTopLeft;
    private JScrollPane myJScrollPaneBottomLeft;
    private JScrollPane myJScrollPaneTopRight;
    private JScrollPane myJScrollPaneBottomRight;

    public JPanel myJPanelCardsTopRight;
    private JPanel myJPanelCard1;
    private JPanel myJPanelCard2;
    private JPanel myJPanelCard3;

    public JTextPane myReadMePanel;

    private JTextPane myTextPaneTopLeft;
    private JTextPane myTextPaneBottomLeft;
    private JTextPane myTextPaneTopRight;
    private JTextPane myTextPaneBottomRight;

    private JPanel myHackPaneTopLeft;
    private JPanel myHackPaneBottomLeft;
    private JPanel myHackPaneBottomRight;

    private JButton myButton11;
    private JButton myButton12;
    private JButton myButton13;
    private JButton myButton14;
    private JButton myButton15;
    private JButton myButton16;

    private JButton myButton31;
    private JButton myButton32;

    private JScrollPane dialogScrollPane;
    private JDialog dialog;
    private JPanel dialogPane;
    private JTextPane dialogTextPane;

    private MyLogger logger;


    public GUI() {
        constructJFrameAndMenus();
        constructTopStripWithButtons();
        constructMainViewWithFourPanes();
        setActionListeners();
        myFrame.setVisible(true);
        mySplitPaneMain.setDividerLocation(0.7);
        mySplitPaneLeft.setDividerLocation(0.5);
        mySplitPaneRight.setDividerLocation(0.9);

        setAboutPanel(MarkdownToStyledDocument.readMarkDownFile());

        myFrame.setVisible(true);

        logger = new MyLogger(myTextPaneBottomRight);
    }

    private void constructJFrameAndMenus() {

        myFrame = new JFrame("Spaghetti - analyzing the structure of Java code");
        myFrame.setLayout(new BorderLayout());

        myFrame.setLocationRelativeTo(null);
        myFrame.setSize(1200,900);
        myFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        myMenuBar = new JMenuBar();
        myFrame.setJMenuBar(myMenuBar);

        fileMenu = new JMenu("File");
        viewMenu = new JMenu("View");
        dbMenu = new JMenu("Database");
        graphMenu = new JMenu("Graphics");
        helpMenu = new JMenu("Help");
        myMenuBar.add(fileMenu);
        myMenuBar.add(viewMenu);
        myMenuBar.add(dbMenu);
        myMenuBar.add(graphMenu);
        myMenuBar.add(helpMenu);
        loadSrcFolderItem = new JMenuItem("Load project folder");
        settingsItem = new JMenuItem("Settings");
        showDBItem = new JMenuItem("Show database");
        clearDBItem = new JMenuItem ("Clear database");
        showGraphItem = new JMenuItem("Show graph");
        howItem = new JMenuItem("How it works");
        aboutItem = new JMenuItem("About");

        loadSrcFolderItem.setName("loadSrcFolderItem");
        settingsItem.setName("settingsItem");
        showDBItem.setName("showDBItem");
        clearDBItem.setName("clearDBItem");
        showGraphItem.setName("showGraphItem");
        howItem.setName("howItem");
        aboutItem.setName("aboutItem");
        fileMenu.add(loadSrcFolderItem);
        fileMenu.add(settingsItem);
        dbMenu.add(showDBItem);
        dbMenu.add(clearDBItem);
        graphMenu.add(showGraphItem);
        helpMenu.add(howItem);
        helpMenu.add(aboutItem);

        settingsItem.setEnabled(false);
        showDBItem.setEnabled(false);
        clearDBItem.setEnabled(false);
        howItem.setEnabled(false);
    }

    private void constructTopStripWithButtons() {

        myTopStrip = new JPanel(new FlowLayout(FlowLayout.LEFT));
        myTopStrip.setBorder(new EmptyBorder(2, 0, 2, 0));

        myButton01 = new JButton("Start animation");
        myButton01.setName("Start_Animation");
        myTopStrip.add(myButton01);
        myButton02 = new JButton("Resume");
        myButton02.setName("Start_Timer");
        myTopStrip.add(myButton02);
        myButton03 = new JButton("Pause");
        myButton03.setName("Stop_Timer");
        myTopStrip.add(myButton03);
        myButton04 = new JButton("Reset");
        myButton04.setName("Reset_Animation");
        myTopStrip.add(myButton04);


        myButton01.setEnabled(false);
        myButton02.setEnabled(false);
        myButton03.setEnabled(false);
        myButton04.setEnabled(false);

        myFrame.add(myTopStrip, BorderLayout.PAGE_START);
    }

    private void constructMainViewWithFourPanes() {

        // every window of JSplitPane has a BasePane in it as first layer
        myBasePaneTopLeft = new JPanel(new BorderLayout());
        myBasePaneBottomLeft = new JPanel(new BorderLayout());
        myBasePaneTopRight = new JPanel(new BorderLayout());
        myBasePaneBottomRight = new JPanel(new BorderLayout());

        // three of four windows have a pane with buttons on their left side
        myButtonPane1 = new JPanel(new FlowLayout());
        myButtonPane1.setPreferredSize(new Dimension(44,200));
        myButton11 = new JButton("A");
        myButton11.setPreferredSize(new Dimension(30,30));
        myButtonPane1.add(myButton11);
        myButton12 = new JButton("B");
        myButton12.setPreferredSize(new Dimension(30,30));
        myButtonPane1.add(myButton12);
        myButton13 = new JButton("C");
        myButton13.setPreferredSize(new Dimension(30,30));
        myButtonPane1.add(myButton13);
        myButton14 = new JButton("D");
        myButton14.setPreferredSize(new Dimension(30,30));
        myButtonPane1.add(myButton14);

        myButtonPane2 = new JPanel(new FlowLayout());
        myButtonPane2.setPreferredSize(new Dimension(44,200));
        myButton15 = new JButton("A");
        myButton15.setPreferredSize(new Dimension(30,30));
        myButtonPane2.add(myButton15);
        myButton16 = new JButton("B");
        myButton16.setPreferredSize(new Dimension(30,30));
        myButtonPane2.add(myButton16);

        myButtonPane3 = new JPanel(new FlowLayout());
        myButtonPane3.setPreferredSize(new Dimension(44,200));
        myButton31 = new JButton("A");
        myButton31.setName("switch_card");
        myButton31.setPreferredSize(new Dimension(30,30));
        myButtonPane3.add(myButton31);
        myButton32 = new JButton("B");
        myButton32.setPreferredSize(new Dimension(30,30));
        myButtonPane3.add(myButton32);

        // three panes have a JTextPane as top layer. This is where the StyledDocument gets into
        myTextPaneTopLeft = new JTextPane();
        myTextPaneTopLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        myTextPaneBottomLeft = new JTextPane();
        myTextPaneBottomLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        myTextPaneBottomRight = new JTextPane();
        myTextPaneBottomRight.setBorder(new EmptyBorder(20, 20, 20, 20));

        // to prevent line wrapping, an additional panel is placed between JScrollPane and JTextPane
        myHackPaneTopLeft = new JPanel(new BorderLayout());
        myHackPaneTopLeft.add(myTextPaneTopLeft, BorderLayout.CENTER);
        myHackPaneBottomLeft = new JPanel(new BorderLayout());
        myHackPaneBottomLeft.add(myTextPaneBottomLeft, BorderLayout.CENTER);
        myHackPaneBottomRight = new JPanel(new BorderLayout());
        myHackPaneBottomRight.add(myTextPaneBottomRight, BorderLayout.CENTER);

        // top right: card layout
        myJPanelCardsTopRight = new JPanel(new CardLayout());
        myJPanelCard1 = new JPanel(new BorderLayout());

        myJPanelCard2 = new JPanel();
        myJPanelCard3 = new JPanel(new BorderLayout());

        myJPanelCardsTopRight.add(myJPanelCard1, "cardReadMe");
        myJPanelCardsTopRight.add(myJPanelCard2, "cardHelp");
        myJPanelCardsTopRight.add(myJPanelCard3, "cardGraphics");

        myJPanelCard1.setBackground(new Color(250,200,200));
        myJPanelCard2.setBackground(new Color(200,250,200));
        myJPanelCard3.setBackground(new Color(200,200,250));

        myReadMePanel = new JTextPane();
        myJScrollPaneTopRight = new JScrollPane(myReadMePanel);
        myReadMePanel.setBorder(new EmptyBorder(50, 70, 20, 50));
        myJPanelCard1.setLayout(new BorderLayout());
        myJPanelCard1.add(myJScrollPaneTopRight, BorderLayout.CENTER);

        CardLayout cl = (CardLayout)(myJPanelCardsTopRight.getLayout());

        myBasePaneTopRight.add(myJPanelCardsTopRight, BorderLayout.CENTER);
        cl.show(myJPanelCardsTopRight, "cardReadMe");

        // three JScrollPanes with 'hackpanes' in them (preventing line wrapping)
        myJScrollPaneTopLeft = new JScrollPane(myHackPaneTopLeft);
        myJScrollPaneTopLeft.getVerticalScrollBar().setUnitIncrement(16);
        myJScrollPaneBottomLeft = new JScrollPane(myHackPaneBottomLeft);
        myJScrollPaneBottomLeft.getVerticalScrollBar().setUnitIncrement(16);
        myJScrollPaneBottomRight = new JScrollPane(myHackPaneBottomRight);
        myJScrollPaneBottomRight.getVerticalScrollBar().setUnitIncrement(16);

        // placing ButtonPanes on the right within their windows
        myBasePaneTopLeft.add(myButtonPane1, BorderLayout.LINE_END);
        myBasePaneBottomLeft.add(myButtonPane2, BorderLayout.LINE_END);
        myBasePaneTopRight.add(myButtonPane3, BorderLayout.LINE_END);

        // placing the JScrollPanes
        myBasePaneTopLeft.add(myJScrollPaneTopLeft, BorderLayout.CENTER);
        myBasePaneBottomLeft.add(myJScrollPaneBottomLeft, BorderLayout.CENTER);
        myBasePaneBottomRight.add(myJScrollPaneBottomRight, BorderLayout.CENTER);

        // three JSplitPanes
        mySplitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myBasePaneTopLeft, myBasePaneBottomLeft);
        mySplitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myBasePaneTopRight, myBasePaneBottomRight);
        mySplitPaneMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mySplitPaneLeft, mySplitPaneRight );

        myFrame.add(mySplitPaneMain, BorderLayout.CENTER);
    }

    private void setActionListeners() {

        loadSrcFolderItem.addActionListener(this);
        settingsItem.addActionListener(this);
        showDBItem.addActionListener(this);
        clearDBItem.addActionListener(this);
        showGraphItem.addActionListener(this);
        howItem.addActionListener(this);
        aboutItem.addActionListener(this);
    }


    public int getWidthUpperRightPanel(){

        return (myJPanelCardsTopRight.getSize().width);
    }

    public int getHeightUpperRightPanel(){

        return (myJPanelCardsTopRight.getSize().height);
    }

    public void setJTextPane(DefaultStyledDocument doc, String which) {
        if (which=="Top") {
            myTextPaneTopLeft.setStyledDocument(doc);
        } else if (which=="Bottom"){
            myTextPaneBottomLeft.setStyledDocument(doc);
        } else if (which=="TopRight"){
            myReadMePanel.setStyledDocument(doc);
        }
    }

    public void setJTextPaneHTML(String str) {
        myTextPaneTopLeft.setText(str);
    }

    public void setGraphicsPanel(JPanel myGraphicsPanel){

        CardLayout cl = (CardLayout)(myJPanelCardsTopRight.getLayout());
        cl.show(myJPanelCardsTopRight, "cardGraphics");
        myJPanelCard3.add(myGraphicsPanel, BorderLayout.CENTER);
    }

    public void setAboutPanel(DefaultStyledDocument doc){
        CardLayout cl = (CardLayout)(myJPanelCardsTopRight.getLayout());
        cl.show(myJPanelCardsTopRight, "cardReadMe");
        myReadMePanel.setStyledDocument(doc);
    }

    public void showWarning(String str) {
        JOptionPane.showConfirmDialog(myFrame, str);
    }

    public void showDialog(String str) {
        dialog = new JDialog(myFrame, "load progress", true);
        dialog.setSize(800,600);
        dialog.setLocationRelativeTo(null);

        dialogPane = new JPanel(new BorderLayout());
        dialogTextPane = new JTextPane();
        dialogTextPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogTextPane.setText(str);
        dialogPane.add(dialogTextPane);
        dialogScrollPane = new JScrollPane(dialogPane);

        dialog.add(dialogScrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JComponent jComponent = (JComponent) e.getSource();
        try {
            new Controller(jComponent.getName(), this, logger);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
