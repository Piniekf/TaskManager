package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Projects;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.ProjectsService;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.service.TaskService;
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

    @Autowired
    private TaskService taskService;

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
        List<Task> allTasks = taskService.getAllTask();
        model.addAttribute("projects", new Projects());
        model.addAttribute("allTasks", allTasks);
        return "create_project";
    }

    @PostMapping("/")
    public String createProject(@ModelAttribute Projects projects,
                                @RequestParam(name = "selectedTasks", required = false) List<Long> selectedTasks) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByEmail(userDetails.getUsername());

        projects.setUser(currentUser);

        if (selectedTasks != null) {
            List<Task> tasks = taskService.getTasksByIds(selectedTasks);
            projects.setTask(tasks);
        }

        projectsService.createNewProject(projects);

        return "redirect:/projects/";
    }

    @GetMapping("/edit_project/{id}")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
        Projects projects = projectsService.findProjectById(id);
        List<Task> allTasks = taskService.getAllTask();
        model.addAttribute("projects", projects);
        model.addAttribute("allTasks", allTasks);
        return "edit_project";
    }

    @PostMapping("/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute Projects projects,
                                @RequestParam(name = "selectedTasks", required = false) List<Long> selectedTasks) {
        Projects existingProject = projectsService.findProjectById(id);
        existingProject.setProjectName(projects.getProjectName());
        existingProject.setProjectDescription(projects.getProjectDescription());
        existingProject.setDueDate(projects.getDueDate());
        existingProject.setCompleted(projects.isCompleted());
        existingProject.setPriority(projects.getPriority());

        if (selectedTasks != null) {
            List<Task> tasks = taskService.getTasksByIds(selectedTasks);
            existingProject.setTask(tasks);
        }

        projectsService.updateProject(existingProject);

        return "redirect:/projects/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectsService.deleteProject(id);
        return "redirect:/projects/";
    }
}
