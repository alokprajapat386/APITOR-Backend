package org.example.apitor.metrics_provider.enums;

public enum AnalyticsTimePeriod {
    DAILY("YYYY-MM-DD"),
    WEEKLY("YYYY-IW");

    private final String dbPattern;

    AnalyticsTimePeriod(String dbPattern){
        this.dbPattern=dbPattern;
    }

    public String getDbPattern(){
        return this.dbPattern;
    }
}
