package com.example.simplyfoundapis.controllers;

import com.example.simplyfoundapis.models.NewsBlog;
import com.example.simplyfoundapis.services.NewsBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = {"https://simplyfound.vercel.app", "http://localhost:8080"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        exposedHeaders = {"Content-Disposition", "Content-Type"},
        allowCredentials = "true")
public class NewsBlogController {

    @Autowired
    private NewsBlogService newsBlogService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadNews(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        try {
            NewsBlog news = newsBlogService.uploadNews(file, title, description);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NewsBlog>> getAllNews() {
        return ResponseEntity.ok(newsBlogService.getAllNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsBlog> getNewsById(@PathVariable int id) {
        return newsBlogService.getNewsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) throws MalformedURLException {
        Resource resource = newsBlogService.loadFileAsResource(fileName);
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
    public ResponseEntity<?> updateNews(
            @PathVariable int id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) throws IOException {
        NewsBlog updated = newsBlogService.updateNews(id, file, title, description);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable int id) {
        newsBlogService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}
