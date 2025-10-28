package com.example.simplyfoundapis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="hero_section")
public class HeroSection {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String tittle;
    private String description;
    private String fileUrl;
    private String fileType;
}
