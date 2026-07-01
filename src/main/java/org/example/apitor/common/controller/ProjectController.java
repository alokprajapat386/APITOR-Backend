package org.example.apitor.common.controller;


import org.example.apitor.common.dto.ProjectCreateRequestDTO;
import org.example.apitor.common.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;
    public ProjectController(ProjectService projectService){
        this.projectService=projectService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody  ProjectCreateRequestDTO createRequest, Authentication auth){

        return ResponseEntity.ok(
            projectService.createProject(createRequest,auth.getName())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id, Authentication auth){

        return ResponseEntity.ok(
            projectService.getProject(id, auth.getName())
        );
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects(Authentication auth){
        
        return ResponseEntity.ok(
            projectService.getAllProjects(auth.getName())
        );
    }

}
