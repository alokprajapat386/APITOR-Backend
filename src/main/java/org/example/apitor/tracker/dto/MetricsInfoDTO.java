package org.example.apitor.tracker.dto;


public record MetricsInfoDTO(
        String ipAddress,
        String requestId,
        String endpointPath,
        String httpMethod,
        Integer statusCode,
        Double latency,
        Long payloadSize,
        String createdAt,
        Double latitude,
        Double longitude

){}
