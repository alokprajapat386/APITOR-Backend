package org.example.apitor.metrics_provider.dto;

public record RouteAnalyticsDTO (
    String endpointPath,
    long requestHits,
    double latencyAvg,
    double latencyP50,
    double latencyP99
){}
