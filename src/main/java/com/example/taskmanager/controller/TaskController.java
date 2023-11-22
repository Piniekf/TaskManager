package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.util.ExportTaskExcel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String createTask(@ModelAttribute Task task){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        task.setUser(currentUser);
        taskService.createNewTask(task);
        return "redirect:/tasks/";
    }

    @GetMapping("/download/{id}")
    public void downloadICSFile(@PathVariable Long id, HttpServletResponse response) {
        Task task = taskService.findTaskById(id);
        if (isCurrentUserOwner(task)) {
            try {
                String icsContent = generateICSContent(task);

                // Ustawienie nagłówków HTTP dla pobierania pliku
                response.setContentType("text/calendar");
                response.setHeader("Content-Disposition", "attachment; filename=" + task.getTaskName() + ".ics");

                // Pobranie OutputStream z HttpServletResponse
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    // Zapisanie zawartości pliku do OutputStream
                    outputStream.write(icsContent.getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace(); // Możesz obsłużyć błąd w odpowiedni sposób
            }
        }
    }

    private String generateICSContent(Task task) {
        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + task.getId() + "\n" +
                "SUMMARY:" + task.getTaskName() + "\n" +
                "DESCRIPTION:" + task.getTaskDescription() + "\n" +
                "DTEND:" + task.getDueDate() + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR\n";
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
        if (isCurrentUserOwner(task)) {
            taskService.deleteTask(id);
        }
        return "redirect:/tasks/";
    }

    private boolean isCurrentUserOwner(Task task) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByEmail(userDetails.getUsername());
        return task.getUser().getId().equals(currentUser.getId());
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=tasks_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Task> listTask = taskService.getAllTask();

        ExportTaskExcel excelExporter = new ExportTaskExcel(listTask);

        excelExporter.export(response);
    }
}
