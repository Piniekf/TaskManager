package com.example.taskmanager.repository;
import com.example.taskmanager.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    List<Projects> findByCompletedTrue();
    List<Projects> findByCompletedFalse();
    List<Projects> findByProjectNameContainingIgnoreCase(String projectName);
    List<Projects> findByProjectDescriptionContainingIgnoreCase(String projectDescription);
    List<Projects> findByPriority(int priority);

}
