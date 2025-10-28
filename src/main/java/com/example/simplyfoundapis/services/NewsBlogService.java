package com.example.simplyfoundapis.services;

import com.example.simplyfoundapis.models.NewsBlog;
import com.example.simplyfoundapis.repositories.NewsBlogRepository;
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
public class NewsBlogService {

    @Autowired
    private NewsBlogRepository newsBlogRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/news/";

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "video/mpeg",
            "video/quicktime"
    );

    public NewsBlog uploadNews(MultipartFile file, String title, String description) throws IOException {
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

        String fileUrl = "https://api.simplyfound.com.na/api/news/view/" + fileName;

        NewsBlog news = new NewsBlog();
        news.setTitle(title);
        news.setDescription(description);
        news.setFileUrl(fileUrl);
        news.setFileType(contentType);
        news.setCreatedDate(LocalDateTime.now());

        return newsBlogRepository.save(news);
    }

    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found or unreadable: " + fileName);
        }
        return resource;
    }

    public List<NewsBlog> getAllNews() {
        return newsBlogRepository.findAll();
    }

    public Optional<NewsBlog> getNewsById(int id) {
        return newsBlogRepository.findById(id);
    }

    public NewsBlog updateNews(int id, MultipartFile file, String title, String description) throws IOException {
        NewsBlog existing = newsBlogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NewsBlog not found"));

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

            String fileUrl = "https://api.simplyfound.com.na/api/news/view/" + fileName;
            existing.setFileUrl(fileUrl);
            existing.setFileType(contentType);
        }

        existing.setTitle(title);
        existing.setDescription(description);

        return newsBlogRepository.save(existing);
    }

    public void deleteNews(int id) {
        NewsBlog news = newsBlogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NewsBlog not found"));
        newsBlogRepository.delete(news);
    }
}
