package org.example.apitor.metrics_provider.dto;

import org.example.apitor.metrics_provider.enums.AnalyticsTimePeriod;

import java.time.Instant;

public record AnalyticsRequestDTO(
        Long projectId,
        String timezone,
        Instant startTime,
        Instant endTime,
        AnalyticsTimePeriod timeGranularity
){}
