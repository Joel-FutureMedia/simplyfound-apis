package com.example.simplyfoundapis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="news_blog")
@Entity
public class NewsBlog {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private LocalDateTime createdDate;

}
