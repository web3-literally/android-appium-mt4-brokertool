package com.mt4.auto.brokertool;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainUI extends JFrame {

    public JButton runAppBtn = null;
    public JButton searchBtn = null;
    public JButton exitBtn = null;
    public JButton findDiffBtn = null;
    public JButton saveSearchBtn = null;

    public JButton oldPathBrowseBtn = null;
    public JButton curPathBrowseBtn = null;
    public JButton diffPathBrowseBtn = null;

    public JTextField oldPathText = null;
    public JTextField curPathText = null;
    public JTextField diffPathText = null;

    JList<String> logList = null;

    public MainUI() {
        super();

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(7,6, 4, 6);
        contentPanel.setBorder(padding);
        this.setContentPane(contentPanel);

        this.getContentPane().setLayout(new BorderLayout(5, 5));

        // Panel with control buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(30, 1, 1, 1));
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));

        findDiffBtn = new JButton("Find Difference And Save");
        fitBtn(findDiffBtn);
        runAppBtn = new JButton("Run MT4 App");
        fitBtn(runAppBtn);
        searchBtn = new JButton("Search MT4 Brokers");
        fitBtn(searchBtn);
        saveSearchBtn = new JButton("Save Search Result");
        fitBtn(saveSearchBtn);
        exitBtn = new JButton("Exit");
        fitBtn(exitBtn);

        btnPanel.add(findDiffBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(runAppBtn);
        btnPanel.add(searchBtn);
        btnPanel.add(saveSearchBtn);
        btnPanel.add(Box.createVerticalStrut(30));
        btnPanel.add(exitBtn);
        this.getContentPane().add(btnPanel, BorderLayout.EAST);

        // Panel with path information
        Dimension lblSize = new Dimension(160, 30);

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.Y_AXIS));

        JPanel oldPanel = new JPanel();
        oldPanel.setLayout(new BorderLayout(4, 1));
        JLabel oldLabel = new JLabel("Previous Saved Information: ");
        oldLabel.setMaximumSize(lblSize);
        oldLabel.setPreferredSize(lblSize);
        oldLabel.setMinimumSize(lblSize);
        oldPanel.add(oldLabel, BorderLayout.WEST);
        oldPathText = new JTextField();
        oldPanel.add(oldPathText, BorderLayout.CENTER);
        oldPathBrowseBtn = new JButton("...");
        oldPanel.add(oldPathBrowseBtn, BorderLayout.EAST);
        pathPanel.add(oldPanel);

        JPanel curPanel = new JPanel();
        curPanel.setLayout(new BorderLayout(4, 1));
        JLabel curLabel = new JLabel("Recently Saved Information: ");
        curLabel.setMinimumSize(lblSize);
        curLabel.setPreferredSize(lblSize);
        curLabel.setMaximumSize(lblSize);
        curPanel.add(curLabel, BorderLayout.WEST);
        curPathText = new JTextField();
        curPanel.add(curPathText, BorderLayout.CENTER);
        curPathBrowseBtn = new JButton("...");
        curPanel.add(curPathBrowseBtn, BorderLayout.EAST);
        pathPanel.add(curPanel);

        JPanel diffPanel = new JPanel();
        diffPanel.setLayout(new BorderLayout(4, 1));
        JLabel diffLabel = new JLabel("Newly Created Servers: ");
        diffLabel.setMinimumSize(lblSize);
        diffLabel.setPreferredSize(lblSize);
        diffLabel.setMaximumSize(lblSize);
        diffPanel.add(diffLabel, BorderLayout.WEST);
        diffPathText = new JTextField();
        diffPanel.add(diffPathText, BorderLayout.CENTER);
        diffPathBrowseBtn = new JButton("...");
        diffPanel.add(diffPathBrowseBtn, BorderLayout.EAST);
        pathPanel.add(diffPanel);
        this.getContentPane().add(pathPanel, BorderLayout.NORTH);

        // Panel with status list
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        logList = new JList<>();
        logList.setModel(new DefaultListModel<String>());
        JScrollPane jsp = new JScrollPane(logList);
        jsp.setBorder(new TitledBorder("Log"));
        statusPanel.add(jsp, BorderLayout.CENTER);
        this.getContentPane().add(statusPanel, BorderLayout.CENTER);

        this.setSize(800, 600);
        this.setTitle("MT4 Broker Tool");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void fitBtn(JButton btn) {
        Dimension btnSize = new Dimension(190, 40);
        Font font = new Font("Arial", Font.PLAIN, 14);
        btn.setPreferredSize(btnSize);
        btn.setMinimumSize(btnSize);
        btn.setMaximumSize(btnSize);
        btn.setFont(font);
    }

    public void updateBtnStatus(int step) {
        runAppBtn.setEnabled(step != Config.PROCESSING_STEP);
        searchBtn.setEnabled(step == Config.APP_STARTED_STEP || step == Config.SEARCH_FINISHED_STEP);
        exitBtn.setEnabled(step != Config.PROCESSING_STEP);
        saveSearchBtn.setEnabled(step == Config.SEARCH_FINISHED_STEP);
        runAppBtn.setText(step == Config.INIT_STEP ? "Run MT4 App" : "Stop MT4 App");
    }

    public void addLog(String msg) {
        DefaultListModel listModel = (DefaultListModel) logList.getModel();
        if (logList.getModel().getSize() > 2000) {
            listModel.removeAllElements();
        }
        listModel.addElement(msg);
    }
}
