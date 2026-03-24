package com.example.practicaspringboot.service;

import com.example.practicaspringboot.domain.Project;
import com.example.practicaspringboot.domain.Role;
import com.example.practicaspringboot.domain.User;
import com.example.practicaspringboot.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /** Returns all projects for admins, or only the owner's projects for regular users. */
    public List<Project> findForUser(User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return projectRepository.findAll();
        }
        return projectRepository.findByOwner(user);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    /** Checks that the given user owns the project (admins bypass). */
    public boolean canAccess(User user, Project project) {
        return user.getRole() == Role.ROLE_ADMIN
                || project.getOwner().getId().equals(user.getId());
    }
}
