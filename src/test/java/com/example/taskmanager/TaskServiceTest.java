package com.example.taskmanager;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(taskId);
        task.setTaskName("Test Task");
        task.setCompleted(true);
    }

    @Test
    void createNewTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task createdTask = taskService.createNewTask(task);
        assertNotNull(createdTask);
        assertEquals(taskId, createdTask.getId());
    }

    @Test
    void getAllTask() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));
        List<Task> tasks = taskService.getAllTask();
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(taskId, tasks.get(0).getId());
    }

    @Test
    void findTaskById() {
        when(taskRepository.getById(taskId)).thenReturn(task);
        Task foundTask = taskService.findTaskById(taskId);
        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getId());
    }

    @Test
    void getTasksByIds() {
        when(taskRepository.findAllById(anyList())).thenReturn(Arrays.asList(task));
        List<Task> tasks = taskService.getTasksByIds(Arrays.asList(taskId));
        assertFalse(tasks.isEmpty());
        assertEquals(taskId, tasks.get(0).getId());
    }

    @Test
    void findAllCompletedTask() {
        when(taskRepository.findByCompletedTrue()).thenReturn(Arrays.asList(task));
        List<Task> tasks = taskService.findAllCompletedTask();
        assertFalse(tasks.isEmpty()); // This line passed, so tasks is not empty
        assertTrue(tasks.get(0).isCompleted()); // This line threw the error
    }


    @Test
    void findAllInCompleteTask() {
        // Setup: Create a task that is incomplete
        Task incompleteTask = new Task();
        incompleteTask.setCompleted(false); // Ensure this is false

        // Mocking: When the repository is asked for incomplete tasks, return the incomplete task
        when(taskRepository.findByCompletedFalse()).thenReturn(Arrays.asList(incompleteTask));

        // Execution: Call the method under test
        List<Task> tasks = taskService.findAllInCompleteTask();

        // Assertion: Expect the returned tasks to be incomplete
        assertFalse(tasks.isEmpty()); // First, check that we have tasks
        assertFalse(tasks.get(0).isCompleted()); // This is the line that's failing
    }

    @Test
    void getFilteredTasks() {
        when(taskRepository.findByCompletedTrue()).thenReturn(Arrays.asList(task));
        when(taskRepository.findByPriority(anyInt())).thenReturn(Arrays.asList(task));
        when(taskRepository.findByTaskNameContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(task));
        when(taskRepository.findByTaskDescriptionContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(task));

        List<Task> filteredByCompletion = taskService.getFilteredTasks(true, null, null, null);
        List<Task> filteredByPriority = taskService.getFilteredTasks(null, 1, null, null);
        List<Task> filteredByName = taskService.getFilteredTasks(null, null, "Test", null);
        List<Task> filteredByDescription = taskService.getFilteredTasks(null, null, null, "Description");

        assertFalse(filteredByCompletion.isEmpty());
        assertFalse(filteredByPriority.isEmpty());
        assertFalse(filteredByName.isEmpty());
        assertFalse(filteredByDescription.isEmpty());
    }

    @Test
    void deleteTask() {
        doNothing().when(taskRepository).deleteById(taskId);
        taskService.deleteTask(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void updateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task updatedTask = taskService.updateTask(task);
        assertNotNull(updatedTask);
        assertEquals(taskId, updatedTask.getId());
    }
}
