package com.example.simplyfoundapis.services;

import com.example.simplyfoundapis.models.Projects;
import com.example.simplyfoundapis.models.Status;
import com.example.simplyfoundapis.repositories.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectsService {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private StatusService statusService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/projects/";

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/mpeg",
            "video/quicktime"
    );

    public Projects uploadProject(MultipartFile file, String projectName, String projectDescription,
                                  String clientName, String projectType, String projectAddress,
                                  LocalDateTime startDate, LocalDateTime completionDate, int statusId) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only images and videos are allowed");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'))
                + "_" + System.currentTimeMillis() + extension;

        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "https://api.simplyfound.com.na/api/projects/view/" + fileName;

        Status status = statusService.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Projects project = new Projects();
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setClientName(clientName);
        project.setProjectType(projectType);
        project.setProjectAddress(projectAddress);
        project.setStartDate(startDate);
        project.setCompletionDate(completionDate);
        project.setFileType(contentType);
        project.setFileUrl(fileUrl);
        project.setStatus(status);

        return projectsRepository.save(project);
    }

    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found or unreadable: " + fileName);
        }
        return resource;
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }

    public Optional<Projects> getProjectById(int id) {
        return projectsRepository.findById(id);
    }

    public List<Projects> getProjectsByStatus(int statusId) {
        return projectsRepository.findByStatusId(statusId);
    }

    public Projects updateProject(int id, MultipartFile file, String projectName, String projectDescription,
                                  String clientName, String projectType, String projectAddress,
                                  LocalDateTime startDate, LocalDateTime completionDate, Integer statusId) throws IOException {

        Projects existing = projectsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Only images and videos are allowed");
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String fileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'))
                    + "_" + System.currentTimeMillis() + extension;

            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "https://api.simplyfound.com.na/api/projects/view/" + fileName;
            existing.setFileUrl(fileUrl);
            existing.setFileType(contentType);
        }

        existing.setProjectName(projectName);
        existing.setProjectDescription(projectDescription);
        existing.setClientName(clientName);
        existing.setProjectType(projectType);
        existing.setProjectAddress(projectAddress);
        existing.setStartDate(startDate);
        existing.setCompletionDate(completionDate);

        if (statusId != null) {
            Status status = statusService.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            existing.setStatus(status);
        }

        return projectsRepository.save(existing);
    }

    public void deleteProject(int id) {
        Projects project = projectsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectsRepository.delete(project);
    }
}
