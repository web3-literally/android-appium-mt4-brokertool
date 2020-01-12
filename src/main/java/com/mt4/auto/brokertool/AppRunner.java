package com.mt4.auto.brokertool;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppRunner {

    public AndroidDriver<MobileElement> driver;
    public WebDriverWait wait;

    public void setup() throws MalformedURLException {
        Logger.info("Setting up connection to emulator...");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", Config.DEVICE_CONFIG.deviceName);
        caps.setCapability("udid", Config.DEVICE_CONFIG.udid);
        caps.setCapability("platformName", Config.DEVICE_CONFIG.platformName);
        caps.setCapability("platformVersion", Config.DEVICE_CONFIG.platformVersion);
        caps.setCapability("skipUnlock", Config.DEVICE_CONFIG.skipUnlock);
        caps.setCapability("appPackage", Config.DEVICE_CONFIG.appPackage);
        caps.setCapability("appActivity", Config.DEVICE_CONFIG.appActivity);
        caps.setCapability("noReset", Config.DEVICE_CONFIG.noReset);

        driver = new AndroidDriver<MobileElement>(new URL(Config.APPIUM_SERVER_URL), caps);
        wait = new WebDriverWait(driver, 60);
    }

    public void preActionForOpenSearchPage() throws Exception {
        Logger.info("Waiting until open account popup disappears...");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("net.metaquotes.metatrader4:id/animation")));
        Logger.info("Open account popup disappeared.");

        Logger.info("Clicking side nav bar");
        MobileElement actionbar_back_icon = driver.findElement(By.id("net.metaquotes.metatrader4:id/actionbar_back_icon"));
        actionbar_back_icon.click();

        Logger.info("Waiting until navbar opens...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("net.metaquotes.metatrader4:id/left_drawer")));
        Logger.info("Navbar opened...");

        Logger.info("Clicking account...");
        MobileElement account_element = driver.findElement(By.id("net.metaquotes.metatrader4:id/company"));
        account_element.click();

        Logger.info("Waiting until account page opens...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("net.metaquotes.metatrader4:id/accounts_list")));
        Logger.info("Account page opened...");

        Logger.info("Clicking account add menuitem...");
        MobileElement account_add_menu = driver.findElement(By.id("net.metaquotes.metatrader4:id/menu_account_add"));
        account_add_menu.click();

        Logger.info("Waiting until new account page opens...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("net.metaquotes.metatrader4:id/login_exist_account")));
        Logger.info("New account page opened...");

        Logger.info("Clicking login existing account button...");
        MobileElement login_existing_account = driver.findElement(By.id("net.metaquotes.metatrader4:id/login_exist_account"));
        login_existing_account.click();

        Logger.info("Waiting until search trader server page opens...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("net.metaquotes.metatrader4:id/filter")));
        Logger.info("Search trader server page opened...");
    }

    public void unsetup() {
        if (driver != null) {
            driver.quit();
        }
    }

    public Map<String, BrokerInfo> setKeyAndSearchBroker(String key) throws Exception {
        Logger.info("Doing search with key [" + key + "] ... ");
        MobileElement filter_edit = driver.findElement(By.id("net.metaquotes.metatrader4:id/filter"));
        filter_edit.setValue(key);
        driver.hideKeyboard();

        Thread.sleep(100);

        Map<String, BrokerInfo> result = new HashMap<>();
        // Get list of servers
        List<MobileElement> server_names = driver.findElements(By.id("net.metaquotes.metatrader4:id/server_name"));
        List<MobileElement> server_titles = driver.findElements(By.id("net.metaquotes.metatrader4:id/server_title"));

        int elementCount = Math.min(server_names.size(), server_titles.size());
        for (int i = 0; i < elementCount; i++) {
            MobileElement server_name = server_names.get(i);
            MobileElement server_title = server_titles.get(i);
            String str_sever_name = server_name.getText();
            String str_server_title = server_title.getText();

            BrokerInfo info = new BrokerInfo(str_sever_name, str_server_title);
            result.put(info.key(), info);
//            Logger.info("Broker server : " + str_sever_name + "\t" + str_server_title);
        }
        Thread.sleep(400);
        return result;
    }
}
