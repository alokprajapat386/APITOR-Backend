package org.example.apitor.common.repository;

import jakarta.persistence.Tuple;
import org.example.apitor.common.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    @Query(value = "SELECT " +
            "m.endpoint_path AS endpointPath, " +
            "COUNT(*) AS apiHitCount, " +
            "AVG(m.latency) AS latencyAvg, " +
            "(percentile_cont(0.50 ) WITHIN GROUP (ORDER BY m.latency)) AS latencyP50, " +
            "(percentile_cont(0.99) WITHIN GROUP (ORDER BY m.latency)) AS latencyP99 " +
            "FROM metrics m " +
            "WHERE m.project_id = :projectId " +
            "AND m.created_at BETWEEN :startTime AND :endTime " +
            "GROUP BY 1",
            nativeQuery = true
    )
    List<Tuple> getRouteBasedAnalytics(@Param("projectId") Long projectId, @Param("startTime") Instant startTime,@Param("endTime") Instant endTime);

    @Query(value = "SELECT to_char(m.created_at AT TIME ZONE :timezone, :formatPattern) AS dateBucket, " +
            "COUNT(*) AS apiHitCount, " +
            "COUNT(DISTINCT m.ip_address) AS uniqueIpCount, " +
            "(percentile_cont(0.50 ) WITHIN GROUP (ORDER BY m.latency)) AS latencyP50, " +
            "(percentile_cont(0.99 ) WITHIN GROUP (ORDER BY m.latency)) AS latencyP99, " +
            "AVG(m.latency) AS latencyAvg, " +
            "AVG(m.payload_size) AS avgPayloadSize, " +
            "MAX(m.payload_size) AS maxPayloadSize " +
            "FROM metrics m " +
            "WHERE m.project_id = :projectId " +
            "AND m.created_at BETWEEN :startTime AND :endTime " +
            "GROUP BY 1 " +
            "ORDER BY 1 ASC",
            nativeQuery = true
    )
    List<Tuple> getCoreAnalytics(@Param("projectId") Long projectId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime,@Param("formatPattern") String formatPattern, @Param("timezone") String timezone);
    @Query(value = "SELECT to_char(m.created_at AT TIME ZONE :timezone, :formatPattern) AS dateBucket, " +
            "m.status_code AS statusCode, " +
            "COUNT(m) AS statusCount " +
            "FROM metrics m " +
            "WHERE m.project_id=:projectId AND m.created_at BETWEEN :startTime AND :endTime " +
            "GROUP BY 1, 2 " +
            "ORDER BY 1 ASC",
            nativeQuery = true
    )
    List<Tuple> getstatusCodesDistribution(@Param("projectId") Long projectId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime,@Param("formatPattern") String formatPattern, @Param("timezone") String timezone);

    @Query(value = " SELECT to_char(m.created_at AT TIME ZONE :timezone, :formatPattern) AS dateBucket, " +
            "m.http_method AS httpMethod, " +
            "COUNT(m) AS httpMethodCount " +
            "FROM metrics m " +
            "WHERE m.project_id=:projectId AND m.created_at BETWEEN :startTime AND :endTime " +
            "GROUP BY 1 , 2 " +
            "ORDER BY 1  ASC",
            nativeQuery = true
    )
    List<Tuple> getHttpMethodsDistribution(@Param("projectId") Long projectId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime,@Param("formatPattern") String formatPattern, @Param("timezone") String timezone);


}
