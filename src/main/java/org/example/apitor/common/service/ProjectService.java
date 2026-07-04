package org.example.apitor.common.service;

import jakarta.transaction.Transactional;
import org.example.apitor.common.dto.ProjectCreateRequestDTO;
import org.example.apitor.common.dto.ProjectDetailsDTO;
import org.example.apitor.common.entity.User;
import org.example.apitor.common.repository.ProjectRepository;
import org.example.apitor.common.entity.Project;
import org.example.apitor.exceptions.ResourceNotFoundException;
import org.example.apitor.security.tracker.ProjectTokenUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProjectService {
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final ProjectTokenUtil projectTokenUtil;
    public ProjectService(UserService userService, ProjectRepository projectRepository, ProjectTokenUtil projectTokenUtil){
        this.userService=userService;
        this.projectRepository=projectRepository;
        this.projectTokenUtil=projectTokenUtil;
    }

    public ProjectDetailsDTO createProject(ProjectCreateRequestDTO projectCreateRequest, String username){
        User user = userService.getUser(username);
        Project project = new Project();
        project.setOwner(user);
        project.setProjectName(projectCreateRequest.projectName());
        project.setTargetURL(projectCreateRequest.targetURL());
        project.setCreatedAt(Instant.now());
        projectRepository.save(project);
        return new ProjectDetailsDTO(project, projectTokenUtil);
    }

    public List<ProjectDetailsDTO> getAllProjects(String username){
        User user = userService.getUser(username);
        return projectRepository.findByOwnerId(user.getId())
                .stream()
                .map((project)-> new ProjectDetailsDTO(project, projectTokenUtil))
                .toList();
    }

    public ProjectDetailsDTO getProject(Long id, String username){
        Project project= projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project with id: " + id + " not found"));
        if(!project.getOwner().getUsername().equals(username)){
            throw new ResourceNotFoundException("Project with id: " + id + " not found");
        }
        return new ProjectDetailsDTO(project,projectTokenUtil);
    }

    public ProjectDetailsDTO updateProject(Long id,ProjectCreateRequestDTO request, String username){
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project with id: " + id + " not found"));
        if(!project.getOwner().getUsername().equals(username)){
            throw new ResourceNotFoundException("Project with id: " + id + " not found");
        }
        if(request.projectName()!=null){
            project.setProjectName(request.projectName());
        }
        if(request.targetURL()!=null){
            project.setTargetURL(request.targetURL());
        }
        projectRepository.save(project);
        return new ProjectDetailsDTO(project, projectTokenUtil);
    }
    @Transactional
    public void deleteProject(Long id, String username){
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project with id: " + id + " not found"));
        if(!project.getOwner().getUsername().equals(username)){
            throw new ResourceNotFoundException("Project with id: " + id + " not found");
        }



        projectRepository.delete(project);
    }




}
