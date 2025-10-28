package com.example.simplyfoundapis.repositories;

import com.example.simplyfoundapis.models.Projects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectsRepository extends JpaRepository<Projects, Integer> {
    List<Projects> findByStatusId(int statusId);
}
