package com.example.simplyfoundapis.controllers;

import com.example.simplyfoundapis.models.Projects;
import com.example.simplyfoundapis.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"https://simplyfound.vercel.app", "http://localhost:5173"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        exposedHeaders = {"Content-Disposition", "Content-Type"},
        allowCredentials = "true")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProject(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectName") String projectName,
            @RequestParam("projectDescription") String projectDescription,
            @RequestParam("clientName") String clientName,
            @RequestParam("projectType") String projectType,
            @RequestParam("projectAddress") String projectAddress,
            @RequestParam("startDate") String startDate,
            @RequestParam("completionDate") String completionDate,
            @RequestParam("statusId") int statusId) {
        try {
            Projects project = projectsService.uploadProject(
                    file, projectName, projectDescription, clientName, projectType,
                    projectAddress, LocalDateTime.parse(startDate), LocalDateTime.parse(completionDate), statusId);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Projects>> getAllProjects() {
        return ResponseEntity.ok(projectsService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projects> getProjectById(@PathVariable int id) {
        return projectsService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Projects>> getProjectsByStatus(@PathVariable int statusId) {
        return ResponseEntity.ok(projectsService.getProjectsByStatus(statusId));
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) throws MalformedURLException {
        Resource resource = projectsService.loadFileAsResource(fileName);
        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        boolean displayInline = contentType.startsWith("image/") || contentType.startsWith("video/") || contentType.equals("application/pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        (displayInline ? "inline" : "attachment") + "; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable int id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("projectName") String projectName,
            @RequestParam("projectDescription") String projectDescription,
            @RequestParam("clientName") String clientName,
            @RequestParam("projectType") String projectType,
            @RequestParam("projectAddress") String projectAddress,
            @RequestParam("startDate") String startDate,
            @RequestParam("completionDate") String completionDate,
            @RequestParam(value = "statusId", required = false) Integer statusId) throws IOException {

        Projects updated = projectsService.updateProject(id, file, projectName, projectDescription,
                clientName, projectType, projectAddress, LocalDateTime.parse(startDate),
                LocalDateTime.parse(completionDate), statusId);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable int id) {
        projectsService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
