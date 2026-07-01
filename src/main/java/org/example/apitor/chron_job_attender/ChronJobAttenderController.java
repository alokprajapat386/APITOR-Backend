package org.example.apitor.chron_job_attender;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChronJobAttenderController {
    @GetMapping("public/ping")
    public ResponseEntity<?> ping(){
        return ResponseEntity.ok("PONG");
    }
}
