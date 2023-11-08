package com.example.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String project;
    private String projectName;
    private String projectDescription;
    private boolean completed;
    @Column(columnDefinition = "DATE")
    private java.sql.Date createdDate;
    @Column(columnDefinition = "DATE")
    private java.sql.Date dueDate;
    @Min(1)
    @Max(3)
    private int priority;
    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User user;
}
