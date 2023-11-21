package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String listTasks(Model model,
                            @RequestParam(name = "completed", required = false) Boolean completed,
                            @RequestParam(name = "priority", required = false) Integer priority,
                            @RequestParam(name = "taskName", required = false) String taskName,
                            @RequestParam(name = "taskDescription", required = false) String taskDescription) {
        List<Task> tasks = taskService.getFilteredTasks(completed, priority, taskName, taskDescription);
        model.addAttribute("tasks", tasks);
        return "list";
    }

    @GetMapping("/create")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "create";
    }

    @PostMapping("/")
    public String createTask(@ModelAttribute Task task) throws IOException {
        // Pobierz zalogowanego użytkownika
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Użyj serwisu użytkowników do znalezienia użytkownika na podstawie email
        User currentUser = userService.findUserByEmail(userDetails.getUsername());

        // Przypisz zalogowanego użytkownika do nowego zadania
        task.setUser(currentUser);

        // Zapisz zadanie do bazy danych
        taskService.createNewTask(task);

        // Stwórz plik ICS z informacjami o zadaniu
        String icsContent = generateICSContent(task);
        saveICSFile(icsContent, task.getTaskName() + ".ics");

        return "redirect:/tasks/";
    }

    private String generateICSContent(Task task) {

        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                // Detale taska
                "BEGIN:VEVENT\n" +
                "UID:" + task.getId() + "@example.com\n" +
                "SUMMARY:" + task.getTaskName() + "\n" +
                "DESCRIPTION:" + task.getTaskDescription() + "\n" +
                "DTEND:" + task.getDueDate() + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
    }

    private void saveICSFile(String icsContent, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(icsContent);
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findTaskById(id);
        if (!isCurrentUserOwner(task)) {
            return "redirect:/tasks/";
        }
        model.addAttribute("task", task);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        Task existingTask = taskService.findTaskById(id);
        // Sprawdzenie czy aktualnie zalogowany użytkownik jest właścicielem zadania, jeśli nie jest to przekieruje go z powrotem
        if (!isCurrentUserOwner(existingTask)) {
            return "redirect:/tasks/";
        }
        existingTask.setTaskName(task.getTaskName());
        existingTask.setTaskDescription(task.getTaskDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setCreatedDate(existingTask.getCreatedDate());
        existingTask.setCompleted(task.isCompleted());
        existingTask.setPriority(task.getPriority());
        taskService.updateTask(existingTask);
        return "redirect:/tasks/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        // Sprawdzenie czy aktualnie zalogowany użytkownik jest właścicielem zadania
        if (isCurrentUserOwner(task)) {
            taskService.deleteTask(id);
        }
        return "redirect:/tasks/";
    }

    // Metoda pomocnicza sprawdzająca, czy aktualnie zalogowany użytkownik jest właścicielem zadania
    private boolean isCurrentUserOwner(Task task) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        return task.getUser().getId().equals(currentUser.getId());
    }


}
