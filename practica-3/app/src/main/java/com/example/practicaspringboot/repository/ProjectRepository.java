package com.example.practicaspringboot.repository;

import com.example.practicaspringboot.domain.Project;
import com.example.practicaspringboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
}
