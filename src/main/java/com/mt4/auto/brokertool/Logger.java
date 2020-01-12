package com.mt4.auto.brokertool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static MainUI mainUI = null;

    public static void setMainUI(MainUI mainUI) {
        Logger.mainUI = mainUI;
    }

    public static void info(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        System.out.println(sdf.format(now) + " ===> " + message);
        if (mainUI != null) {
            mainUI.addLog(sdf.format(now) + " ===> " + message);
        }
    }

    public static void error(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        System.err.println( sdf.format(now) + " ===> " + message);
        if (mainUI != null) {
            mainUI.addLog(sdf.format(now) + " ===> " + message);
        }
    }
}
