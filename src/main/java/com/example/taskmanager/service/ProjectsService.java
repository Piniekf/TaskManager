package com.example.taskmanager.service;
import com.example.taskmanager.entity.Projects;
import com.example.taskmanager.repository.ProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectsService {
    @Autowired
    private ProjectsRepository projectsRepository;

    public Projects createNewProject(Projects projects) {
        return projectsRepository.save(projects);
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }

    public Projects findProjectById(Long id) {
        return projectsRepository.getById(id);
    }

    public List<Projects> findAllCompletedProjects() {
        return projectsRepository.findByCompletedTrue();
    }

    public List<Projects> findAllInCompleteProjects() {
        return projectsRepository.findByCompletedFalse();
    }

    public List<Projects> getFilteredProjects(Boolean completed, Integer priority, String projectName, String projectDescription) {
        if (completed != null) {
            return completed ? findAllCompletedProjects() : findAllInCompleteProjects();
        } else if (priority != null) {
            return projectsRepository.findByPriority(priority);
        } else if (projectName != null) {
            return projectsRepository.findByProjectNameContainingIgnoreCase(projectName);
        } else if (projectDescription != null) {
            return projectsRepository.findByProjectDescriptionContainingIgnoreCase(projectDescription);
        } else {
            return getAllProjects();
        }
    }

    public void deleteProject(Long id) {
        projectsRepository.deleteById(id);
    }

    public Projects updateProject(Projects projects) {
        return projectsRepository.save(projects);
    }
}
