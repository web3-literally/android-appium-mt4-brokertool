package com.mt4.auto.brokertool;

import sun.rmi.runtime.Log;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MainController {

    private MainUI mainUI = null;
    private AppRunner appRunner = null;
    private boolean bAppStarted = false;
    private Map<String, BrokerInfo> searchResult = new HashMap<>();

    public MainController() {
        appRunner = new AppRunner();
        mainUI = new MainUI();

        initActionListener();
        Logger.setMainUI(mainUI);
        mainUI.updateBtnStatus(Config.INIT_STEP);
    }

    private void initActionListener() {

        mainUI.oldPathBrowseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browsePath(Config.BROWSE_OLD_PATH);
            }
        });

        mainUI.curPathBrowseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browsePath(Config.BROWSE_CUR_PATH);
            }
        });

        mainUI.diffPathBrowseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browsePath(Config.BROWSE_DIFF_PATH);
            }
        });

        mainUI.findDiffBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findDiffAndSave();
            }
        });

        mainUI.runAppBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runOrStopApp();
            }
        });

        mainUI.searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBroker();
            }
        });

        mainUI.saveSearchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSearchResult();
            }
        });

        mainUI.exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitProject();
            }
        });

        mainUI.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                exitProject();
            }
        });
    }

    private void browsePath(int type) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle(type == Config.BROWSE_OLD_PATH
                ? "Choose previous recorded file: "
                : type == Config.BROWSE_CUR_PATH
                    ? "Choose recently recorded file: "
                    : "Select path to save differences: ");
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Brokers information files (*.csv)", "csv"));

        if (type == Config.BROWSE_OLD_PATH && jfc.showOpenDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            mainUI.oldPathText.setText(jfc.getSelectedFile().getAbsolutePath());
        } else if (type == Config.BROWSE_CUR_PATH && jfc.showOpenDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            mainUI.curPathText.setText(jfc.getSelectedFile().getAbsolutePath());
        } else if (type == Config.BROWSE_DIFF_PATH && jfc.showSaveDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            String selectedPath = jfc.getSelectedFile().getAbsolutePath();
            if (!selectedPath.endsWith(".csv")) {
                selectedPath += ".csv";
            }
            if (new File(selectedPath).exists()
                    && JOptionPane.showConfirmDialog(mainUI, "Same file exists. Do you want to overwrite it?") != JOptionPane.YES_OPTION) {
                return;
            }
            mainUI.diffPathText.setText(selectedPath);
        }
    }

    private void findDiffAndSave() {
        if (mainUI.oldPathText.getText().isEmpty() || mainUI.curPathText.getText().isEmpty() || mainUI.diffPathText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(mainUI, "Please select all file paths", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BufferedReader oldReader = new BufferedReader(new FileReader(mainUI.oldPathText.getText()));
            BufferedReader curReader = new BufferedReader(new FileReader(mainUI.curPathText.getText()));
            FileOutputStream diffFos = new FileOutputStream(new File(mainUI.diffPathText.getText()));

            Map<String, BrokerInfo> oldMap = new HashMap<>();
            String line = null;

            while ((line = oldReader.readLine()) != null) {
                String str[] = line.split(",");
                if (str.length < 3) {
                    continue;
                }
                BrokerInfo item = new BrokerInfo(str[0], str[1], str[2]);
                oldMap.put(item.key(), item);
            }

            while ((line = curReader.readLine()) != null) {
                String str[] = line.split(",");
                if (str.length < 3) {
                    continue;
                }
                BrokerInfo item = new BrokerInfo(str[0], str[1], str[2]);
                if (oldMap.containsKey(item.key())) {
                    continue;
                }
                line += "\n";
                diffFos.write(line.getBytes("UTF-8"));
            }

            oldReader.close();
            curReader.close();
            diffFos.close();
            JOptionPane.showMessageDialog(mainUI, "Differences are saved into selected file", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainUI, "Find diff failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runOrStopApp() {
        mainUI.updateBtnStatus(Config.PROCESSING_STEP);
        RunOrStopAppService service = new RunOrStopAppService();
        CompletableFuture<Boolean> f = CompletableFuture.supplyAsync(service::runAsync);
        f.thenAccept(this::runOrStopFinished);
    }

    private void runOrStopFinished(Boolean success) {
        mainUI.updateBtnStatus(bAppStarted ? Config.INIT_STEP : Config.APP_STARTED_STEP);
        bAppStarted = !bAppStarted;
    }

    private void searchBroker() {
        mainUI.updateBtnStatus(Config.SEARCHING_STEP);
        searchResult.clear();
        SearchService service = new SearchService();
        CompletableFuture<Map<String, BrokerInfo>> f = CompletableFuture.supplyAsync(service::runAsync);
        f.thenAccept(this::searchFinished);
    }

    private void searchFinished(Map<String, BrokerInfo> searchResult) {
        mainUI.updateBtnStatus(Config.SEARCH_FINISHED_STEP);
        this.searchResult = searchResult;
    }

    private void saveSearchResult() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select path to save search result: ");
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Brokers information files (*.csv)", "csv"));

        if (jfc.showSaveDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            String selectedPath = jfc.getSelectedFile().getAbsolutePath();
            if (!selectedPath.endsWith(".csv")) {
                selectedPath += ".csv";
            }
            File file = new File(selectedPath);
            if (file.exists()
                    && JOptionPane.showConfirmDialog(mainUI, "Same file exists. Do you want to overwrite it?") != JOptionPane.YES_OPTION) {
                return;
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                String header = "Added Date,Broker Name,Broker Title\n";
                fos.write(header.getBytes("UTF-8"));
                for (String key : searchResult.keySet()) {
                    fos.write(searchResult.get(key).toCSVString().getBytes("UTF-8"));
                }
                JOptionPane.showMessageDialog(mainUI, "Search result saved", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainUI, "Save result failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exitProject() {
        Logger.info("Broker Tool exited...\nThanks for using our product!");
        System.exit(0);
    }

    class SearchService {
        Map<String, BrokerInfo> runAsync() {
            Map<String, BrokerInfo> searchResults = new HashMap<>();
            try {
                for (char ch_1st = 'a'; ch_1st <= 'z'; ch_1st++) {
                    for (char ch_2nd = 'a'; ch_2nd <= 'z'; ch_2nd++) {
//                for (char ch_1st = 'a'; ch_1st <= 'a'; ch_1st++) {
//                    for (char ch_2nd = 'a'; ch_2nd <= 'f'; ch_2nd++) {
                        String key = String.valueOf(ch_1st) + String.valueOf(ch_2nd);
                        Map<String, BrokerInfo> result = appRunner.setKeyAndSearchBroker(key);
                        searchResults.putAll(result);
                    }
                }
            } catch (Exception e) {
                Logger.error("Exception when search broker: " + e.getLocalizedMessage());
            }

            return searchResults;
        }
    }

    class RunOrStopAppService {
        Boolean runAsync() {
            if (bAppStarted) {
                appRunner.unsetup();
            } else {
                try {
                    appRunner.setup();
                    appRunner.preActionForOpenSearchPage();
                } catch (Exception e) {
                    Logger.error("Exception when setup AppRunner: " + e.getLocalizedMessage());
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            DarkTheme.init();
        } catch (Exception e) {
            Logger.error("Exception in setting dark theme: " + e.getLocalizedMessage());
        }
        new MainController();
    }
}


