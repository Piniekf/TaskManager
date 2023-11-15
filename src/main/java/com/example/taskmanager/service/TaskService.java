package com.example.taskmanager.service;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task createNewTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    public Task findTaskById(Long id) {
        return taskRepository.getById(id);
    }

    public List<Task> findAllCompletedTask() {
        return taskRepository.findByCompletedTrue();
    }

    public List<Task> findAllInCompleteTask() {
        return taskRepository.findByCompletedFalse();
    }

    public List<Task> getFilteredTasks(Boolean completed, Integer priority, String taskName, String taskDescription) {
        if (completed != null) {
            return completed ? findAllCompletedTask() : findAllInCompleteTask();
        } else if (priority != null) {
            return taskRepository.findByPriority(priority);
        } else if (taskName != null) {
            return taskRepository.findByTaskNameContainingIgnoreCase(taskName);
        } else if (taskDescription != null) {
            return taskRepository.findByTaskDescriptionContainingIgnoreCase(taskDescription);
        } else {
            return getAllTask();
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
}
