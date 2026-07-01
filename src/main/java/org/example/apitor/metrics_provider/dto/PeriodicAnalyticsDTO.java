package org.example.apitor.metrics_provider.dto;

import java.util.Map;

public record PeriodicAnalyticsDTO (
        String periodLabel,
        long apiHitCount,
        long uniqueIpCount,
        double latencyAvg,
        double latencyP50,
        double latencyP99,
        Map<Integer, Long> statusCodesFrequencies,
        Map<String, Long> httpMethodFrequencies,
        double avgPayloadSize,
        long maxPayloadSize

){}
