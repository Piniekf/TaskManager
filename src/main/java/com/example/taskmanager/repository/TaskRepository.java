package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedTrue();
    List<Task> findByCompletedFalse();
    List<Task> findByTaskNameContainingIgnoreCase(String taskName);
    List<Task> findByTaskDescriptionContainingIgnoreCase(String taskDescription);
    List<Task> findByPriority(int priority);
}
