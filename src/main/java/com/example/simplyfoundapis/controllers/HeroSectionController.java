package com.example.simplyfoundapis.controllers;

import com.example.simplyfoundapis.models.HeroSection;
import com.example.simplyfoundapis.services.HeroSectionService;
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
@RequestMapping("/api/hero")
@CrossOrigin(origins = {"https://simplyfound.vercel.app", "https://newgate.simplyfound.com.na","https://newgate.simplyfound.com.na/admin","https://newgateinvestments.simplyfound.com.na"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        exposedHeaders = {"Content-Disposition", "Content-Type"},
        allowCredentials = "true")
public class HeroSectionController {

    @Autowired
    private HeroSectionService heroSectionService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadHero(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        try {
            HeroSection hero = heroSectionService.uploadHero(file, title, description);
            return ResponseEntity.ok(hero);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<HeroSection>> getAllHeroes() {
        return ResponseEntity.ok(heroSectionService.getAllHeroes());
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) throws MalformedURLException {
        Resource resource = heroSectionService.loadFileAsResource(fileName);
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
    public ResponseEntity<?> updateHero(
            @PathVariable int id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) throws IOException {
        HeroSection updated = heroSectionService.updateHero(id, file, title, description);
        return ResponseEntity.ok(updated); //
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable int id) {
        heroSectionService.deleteHero(id);
        return ResponseEntity.noContent().build();
    }
}
