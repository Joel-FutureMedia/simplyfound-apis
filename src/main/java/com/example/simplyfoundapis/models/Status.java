package com.example.simplyfoundapis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="status")
public class Status {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String statusname;

}
