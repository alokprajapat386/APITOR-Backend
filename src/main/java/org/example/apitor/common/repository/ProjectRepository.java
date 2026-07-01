package org.example.apitor.common.repository;

import org.example.apitor.common.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectKey(UUID projectKey);
    List<Project> findByOwnerId(Long id);
}
