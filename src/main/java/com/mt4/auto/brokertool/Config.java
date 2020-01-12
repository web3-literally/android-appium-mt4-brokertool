package com.mt4.auto.brokertool;

public class Config {

    public static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723/wd/hub";
    public static final DeviceConfig DEVICE_CONFIG = new DeviceConfig();

    public static final int INIT_STEP = 0x01;
    public static final int APP_STARTED_STEP = 0x02;
    public static final int SEARCHING_STEP = 0x03;
    public static final int SEARCH_FINISHED_STEP = 0x04;
    public static final int PROCESSING_STEP = 0x05;

    public static final int BROWSE_OLD_PATH = 0x01;
    public static final int BROWSE_CUR_PATH = 0x02;
    public static final int BROWSE_DIFF_PATH = 0x03;
}

class DeviceConfig {
    public String deviceName = "NoxPlayer 6.6.0.0";
    public String udid = "127.0.0.1:62001";
    public String platformName = "Android";
    public String platformVersion = "5.1.1";
    public String appPackage = "net.metaquotes.metatrader4";
    public String appActivity = "net.metaquotes.metatrader4.ui.MainActivity";
    public String skipUnlock = "true";
    public String noReset = "false";
}

