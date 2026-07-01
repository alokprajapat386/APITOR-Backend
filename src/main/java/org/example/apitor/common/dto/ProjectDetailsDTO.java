package org.example.apitor.common.dto;

import org.example.apitor.common.entity.Project;
import org.example.apitor.security.tracker.ProjectTokenUtil;

import java.time.Instant;


public record ProjectDetailsDTO (
     Long id,

     String projectName,
//     UUID projectKey,
     String projectToken,
     String targetURL,
     Instant createdAt
){



    public ProjectDetailsDTO(Project project, ProjectTokenUtil projectTokenUtil) {


        this(
                project.getId(),
                project.getProjectName(),
                projectTokenUtil.generateProjectToken(project.getProjectKey().toString()),
                project.getTargetURL(),
                project.getCreatedAt()
        );
    }


}
