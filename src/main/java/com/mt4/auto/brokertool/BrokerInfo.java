package com.mt4.auto.brokertool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BrokerInfo {
    public String brokerName = "";
    public String brokerTitle = "";
    public String searchedDate = "";

    public BrokerInfo(String brokerName, String brokerTitle) {
        this.brokerName = brokerName;
        this.brokerTitle = brokerTitle;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        searchedDate = sdf.format(new Date());
    }

    public BrokerInfo(String searchedDate, String brokerName, String brokerTitle) {
        this.brokerName = brokerName;
        this.brokerTitle = brokerTitle;
        this.searchedDate = searchedDate;
    }

    public String key() {
        return brokerName + "**" + brokerTitle;
    }

    public boolean isSame(BrokerInfo other) {
        return brokerName.equals(other.brokerName) && brokerTitle.equals(other.brokerTitle);
    }

    public String toCSVString() {
        return searchedDate + "," + brokerName + "," + brokerTitle + "\n";
    }
}
