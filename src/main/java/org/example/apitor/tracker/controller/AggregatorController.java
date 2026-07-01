package org.example.apitor.tracker.controller;

import org.example.apitor.tracker.dto.MetricsInfoDTO;
import org.example.apitor.tracker.service.MetricsAggregatorService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/aggregator")
public class AggregatorController {

    private final MetricsAggregatorService aggregatorService;
    public AggregatorController(MetricsAggregatorService aggregatorService){
        this.aggregatorService=aggregatorService;
    }

    @PostMapping
    public void postMetrics(
            @RequestBody MetricsInfoDTO metricsInfo,
            Authentication auth
    ){

        aggregatorService.addMetric(metricsInfo, UUID.fromString(auth.getName()));
    }

}
