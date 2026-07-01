package org.example.apitor.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "projects",
    indexes = {
        @Index(name = "idx_project_owner_id", columnList = "owner_id")
    }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "owner_id", nullable = false)
    private User owner;

    @UuidGenerator
    @Column(unique = true, nullable = false)
    private UUID projectKey;

    private String projectName;

    @Column(nullable = false)
    private String targetURL;

    @Column(nullable=false)
    private Instant createdAt;

}
