package com.example.simplyfoundapis.repositories;

import com.example.simplyfoundapis.models.NewsBlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsBlogRepository extends JpaRepository<NewsBlog, Integer> {
}
