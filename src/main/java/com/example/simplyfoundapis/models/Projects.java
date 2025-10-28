package com.example.simplyfoundapis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="projects")
public class Projects {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String projectName;
    private String projectDescription;
    private String clientName;
    private String projectType;
    private String projectAddress;
    private LocalDateTime startDate;
    private LocalDateTime CompletionDate;
    private String fileUrl;
    private String fileType;
    @ManyToOne
    private Status status;

}
