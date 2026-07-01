package org.example.apitor.metrics_provider.controller;

import org.example.apitor.metrics_provider.dto.AnalyticsRequestDTO;
import org.example.apitor.metrics_provider.enums.AnalyticsTimePeriod;
import org.example.apitor.metrics_provider.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/analytics")
public class MetricsProviderController {
    private final MetricsService metricsService;
    public MetricsProviderController(MetricsService metricsService){
        this.metricsService=metricsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMetricsAnalytics(@PathVariable Long id, @RequestParam(defaultValue = "DAILY") AnalyticsTimePeriod analyticsTimePeriod, Authentication auth, @RequestParam(required = false )Instant startTime, @RequestParam(required = false) Instant endTime, @RequestParam(defaultValue = "Asia/Kolkata") String timezone){

        AnalyticsRequestDTO analyticsRequest= new AnalyticsRequestDTO(
                    id,
                timezone,
                (startTime==null? (endTime==null? Instant.now(): endTime).minus(Duration.ofDays(7)): startTime ),
                (endTime==null ? Instant.now(): endTime),
                analyticsTimePeriod
                );

        return ResponseEntity.ok(
                metricsService.getAnalytics(analyticsRequest, auth.getName())
        );
    }

}
