package org.example.apitor.tracker.service;

import org.example.apitor.common.entity.Metric;
import org.example.apitor.common.repository.MetricRepository;
import org.example.apitor.common.repository.ProjectRepository;
import org.example.apitor.exceptions.ResourceNotFoundException;
import org.example.apitor.external.service.LocationService;
import org.example.apitor.tracker.dto.MetricsInfoDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

@Service
public class MetricsAggregatorService {
    private final MetricRepository metricRepository;
    private final ProjectRepository projectRepository;
    private final LocationService locationService;
    MetricsAggregatorService(MetricRepository metricRepository, ProjectRepository projectRepository, LocationService locationService ){
        this.metricRepository=metricRepository;
        this.projectRepository=projectRepository;
        this.locationService=locationService;
    }

    public void addMetric(MetricsInfoDTO metricsInfo,
                          UUID projectKey){
        Metric metric = new Metric();
        metric.setProject(projectRepository.findByProjectKey(projectKey).
                orElseThrow(()-> new ResourceNotFoundException("Project with projectKey: " + projectKey + " not found"))
        );
        metric.setRequestId(metricsInfo.requestId());
        metric.setEndpointPath(metricsInfo.endpointPath());
        metric.setHttpMethod(metricsInfo.httpMethod());
        metric.setStatusCode(metricsInfo.statusCode());
        metric.setLatency(metricsInfo.latency());
        metric.setPayloadSize(metricsInfo.payloadSize());
        try {
            metric.setCreatedAt(Instant.parse(metricsInfo.createdAt()));
        }catch(DateTimeParseException ex){
            metric.setCreatedAt(Instant.now());
        }

        Map<String, String> location= locationService.resolveLocationFromIp(metricsInfo.ipAddress());
        metric.setCountry(location.get("country"));
        metric.setCity(location.get("city"));
        metric.setLatitude(metricsInfo.latitude()==null?Double.parseDouble(location.get("latitude")): metricsInfo.latitude());
        metric.setLongitude(metricsInfo.longitude()==null?Double.parseDouble(location.get("longitude")): metricsInfo.longitude());

        metricRepository.save(metric);

    }
}
