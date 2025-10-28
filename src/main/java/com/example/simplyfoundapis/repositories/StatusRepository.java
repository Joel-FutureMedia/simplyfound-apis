package com.example.simplyfoundapis.repositories;

import com.example.simplyfoundapis.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
}
