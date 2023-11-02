package com.example.taskmanager.repository;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    User findByID(Long ID);
}
