package com.example.taskmanager.controller;
import com.example.taskmanager.entity.Projects;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.ProjectsService;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String listProjects(Model model,
                               @RequestParam(name = "completed", required = false) Boolean completed,
                               @RequestParam(name = "priority", required = false) Integer priority,
                               @RequestParam(name = "projectName", required = false) String projectName,
                               @RequestParam(name = "projectDescription", required = false) String projectDescription) {
        List<Projects> projects = projectsService.getFilteredProjects(completed, priority, projectName, projectDescription);
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/create_project")
    public String showCreateProjectForm(Model model) {
        model.addAttribute("projects", new Projects());
        return "create_project";
    }

    @PostMapping("/")
    public String createProject(@ModelAttribute Projects projects) {
        // Pobierz zalogowanego użytkownika
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Użyj serwisu użytkowników do znalezienia użytkownika na podstawie email
        User currentUser = userService.findUserByEmail(userDetails.getUsername());

        // Przypisz zalogowanego użytkownika do nowego zadania
        projects.setUser(currentUser);

        // Zapisz zadanie do bazy danych
        projectsService.createNewProject(projects);

        return "redirect:/projects/";
    }

    @GetMapping("/edit_project/{id}")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
        Projects projects = projectsService.findProjectById(id);
        model.addAttribute("projects", projects);
        return "edit_project";
    }

    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute Projects projects) {
        Projects existingProject = projectsService.findProjectById(id);
        existingProject.setProjectName(projects.getProjectName());
        existingProject.setProjectDescription(projects.getProjectDescription());
        existingProject.setDueDate(projects.getDueDate());
        existingProject.setCreatedDate(existingProject.getCreatedDate());
        existingProject.setCompleted(projects.isCompleted());
        existingProject.setPriority(projects.getPriority());
        projectsService.updateProject(existingProject);
        return "redirect:/projects/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectsService.deleteProject(id);
        return "redirect:/projects/";
    }
}

