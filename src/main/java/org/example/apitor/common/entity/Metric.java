package org.example.apitor.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Table(name = "metrics",
    indexes = {
        @Index(name = "idx_metrics_project_id", columnList = "project_id, createdAt ASC")
    }
)
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    private String requestId;

    private String ipAddress;
    private String endpointPath;
    private String httpMethod;
    private Integer statusCode;
    private Double latency;
    private Long payloadSize;

    private Instant createdAt;

    // Location
    private String country;
    private String city;
    private Integer pinCode;
    private Double latitude;
    private Double longitude;

}
