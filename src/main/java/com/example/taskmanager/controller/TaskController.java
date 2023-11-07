package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

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
    public String createTask(@ModelAttribute Task task) {
        taskService.createNewTask(task);
        return "redirect:/tasks/";
    }

    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Task task = taskService.findTaskById(id);
        model.addAttribute("task", task);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        Task existingTask = taskService.findTaskById(id);
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
        taskService.deleteTask(id);
        return "redirect:/tasks/";
    }
}
