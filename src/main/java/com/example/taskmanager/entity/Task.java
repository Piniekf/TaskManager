package com.example.taskmanager.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String task;
    private String taskName;
    private String taskDescription;
    private boolean completed;
    @Column(columnDefinition = "DATE")
    private java.sql.Date createdDate;
    @Column(columnDefinition = "DATE")
    private java.sql.Date dueDate;
}
