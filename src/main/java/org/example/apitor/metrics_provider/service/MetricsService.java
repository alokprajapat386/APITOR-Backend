package org.example.apitor.metrics_provider.service;

import jakarta.persistence.Tuple;
import org.example.apitor.common.dto.ProjectDetailsDTO;
import org.example.apitor.common.repository.MetricRepository;
import org.example.apitor.common.service.ProjectService;
import org.example.apitor.metrics_provider.dto.AnalyticsRequestDTO;
import org.example.apitor.metrics_provider.dto.MetricsAnalyticsDTO;
import org.example.apitor.metrics_provider.dto.PeriodicAnalyticsDTO;
import org.example.apitor.metrics_provider.dto.RouteAnalyticsDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final ProjectService projectService;
    private final MetricRepository metricRepository;
    public MetricsService(
            ProjectService projectService,
            MetricRepository metricRepository
    ){
        this.projectService=projectService;
        this.metricRepository=metricRepository;
    }

    public MetricsAnalyticsDTO getAnalytics(AnalyticsRequestDTO analyticsRequest, String username){
        if(analyticsRequest.startTime().isAfter(analyticsRequest.endTime())){
            throw new IllegalArgumentException("Can't process request: StartTime must be smaller than EndTime");
        }
        ProjectDetailsDTO project = projectService.getProject(analyticsRequest.projectId(), username);

        return new MetricsAnalyticsDTO(
                getPeriodicAnalytics(project.id(), analyticsRequest.startTime(), analyticsRequest.endTime(), analyticsRequest.timeGranularity().getDbPattern(), analyticsRequest.timezone()),
                getRouteBasedAnalytics(project.id(), analyticsRequest.startTime(), analyticsRequest.endTime())
        );
    }

    private List<RouteAnalyticsDTO> getRouteBasedAnalytics (Long projectId, Instant startTime, Instant endTime){
        List<Tuple> routeAnalytics =  metricRepository.getRouteBasedAnalytics(
                projectId,
                startTime,
                endTime
        );

        return routeAnalytics.stream()
                .map((routeAnalytic)-> new RouteAnalyticsDTO(
                        routeAnalytic.get("endpointPath", String.class),
                        routeAnalytic.get("apiHitCount", Long.class),
                        routeAnalytic.get("latencyAvg", Double.class),
                        routeAnalytic.get("latencyP50", Double.class),
                        routeAnalytic.get("latencyP99", Double.class)
                )).toList();

    }

    private List<PeriodicAnalyticsDTO> getPeriodicAnalytics(Long projectId, Instant startTime, Instant endTime, String dbPattern, String timezone){
        List<Tuple> coreAnalytics = metricRepository.getCoreAnalytics(projectId, startTime, endTime, dbPattern, timezone);
        List<Tuple> statusRows = metricRepository.getstatusCodesDistribution(projectId, startTime, endTime, dbPattern, timezone);
        List<Tuple> httpMethodRows = metricRepository.getHttpMethodsDistribution(projectId, startTime, endTime, dbPattern, timezone);

        Map<String, Map<Integer, Long>> statusLookup = statusRows.stream()
                .collect(Collectors.groupingBy(
                        row-> row.get("dateBucket", String.class),
                        Collectors.toMap(
                                row->row.get("statusCode", Integer.class),
                                row->row.get("statusCount", Long.class)
                        )
                ));

        Map<String, Map<String, Long>> httpMethodLookUp = httpMethodRows.stream()
                .collect(Collectors.groupingBy(
                        row-> row.get("dateBucket", String.class),
                        Collectors.toMap(
                                row->row.get("httpMethod", String.class),
                                row->row.get("httpMethodCount", Long.class)
                        )
                ));

        return  coreAnalytics.stream()
                .map(coreAnalytic->{
                    String date= coreAnalytic.get("dateBucket", String.class);

                    Map<Integer, Long> statusMap=statusLookup.getOrDefault(date, Map.of());
                    Map<String, Long> httpMethodMap=httpMethodLookUp.getOrDefault(date, Map.of());

                    return new PeriodicAnalyticsDTO(
                            date,
                            coreAnalytic.get("apiHitCount",Long.class),
                            coreAnalytic.get("uniqueIpCount", Long.class),
                            coreAnalytic.get("latencyAvg", Double.class),
                            coreAnalytic.get("latencyP50", Double.class),
                            coreAnalytic.get("latencyP99", Double.class),
                            statusMap,
                            httpMethodMap,
                            coreAnalytic.get("avgPayloadSize", BigDecimal.class).doubleValue(),
                            coreAnalytic.get("maxPayloadSize", Long.class)
                    );

                })
                .toList();

    }
}
