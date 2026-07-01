package org.example.apitor.metrics_provider.dto;

import java.util.List;

public record MetricsAnalyticsDTO(
    List<PeriodicAnalyticsDTO> periodicAnalytics,
    List<RouteAnalyticsDTO> routeAnalytics

){}
