package com.example.simplyfoundapis.services;

import com.example.simplyfoundapis.models.HeroSection;
import com.example.simplyfoundapis.repositories.HeroSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;

@Service
public class HeroSectionService {

    @Autowired
    private HeroSectionRepository heroSectionRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/hero/";

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/mpeg",
            "video/quicktime"
    );

    public HeroSection uploadHero(MultipartFile file, String title, String description) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType))
            throw new IllegalArgumentException("Only images and videos are allowed");

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'))
                + "_" + System.currentTimeMillis() + extension;

        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "https://simplyfound.vercel.app/api/hero/view/" + fileName;

        HeroSection hero = new HeroSection();
        hero.setTittle(title);
        hero.setDescription(description);
        hero.setFileUrl(fileUrl);
        hero.setFileType(contentType);

        return heroSectionRepository.save(hero);
    }

    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found or unreadable: " + fileName);
        }
        return resource;
    }

    public List<HeroSection> getAllHeroes() {
        return heroSectionRepository.findAll();
    }

    public HeroSection updateHero(int id, MultipartFile file, String title, String description) throws IOException {
        HeroSection existing = heroSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeroSection not found"));

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType))
                throw new IllegalArgumentException("Only images and videos are allowed");

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String fileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'))
                    + "_" + System.currentTimeMillis() + extension;

            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "https://simplyfound.vercel.app/api/hero/view/" + fileName;
            existing.setFileUrl(fileUrl);
            existing.setFileType(contentType);
        }

        existing.setTittle(title);
        existing.setDescription(description);

        return heroSectionRepository.save(existing);
    }

    public void deleteHero(int id) {
        HeroSection hero = heroSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeroSection not found"));
        heroSectionRepository.delete(hero);
    }
}
