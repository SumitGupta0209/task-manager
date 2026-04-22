package com.sumit.taskmanager.service;

import com.sumit.taskmanager.dto.TaskResponseDto;
import com.sumit.taskmanager.eums.Priority;
import com.sumit.taskmanager.eums.TaskStatus;
import com.sumit.taskmanager.exception.ResourceNotFoundException;
import com.sumit.taskmanager.exception.UnauthorizedException;
import com.sumit.taskmanager.model.Task;
import com.sumit.taskmanager.model.User;
import com.sumit.taskmanager.repository.TaskRepository;
import com.sumit.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @CacheEvict(value = "userTasks", allEntries = true)
    public Task createTask(Task task, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (task.getDueDate().isBefore(LocalDateTime.now().plusDays(1))) {
            task.setPriority(Priority.HIGH);
        }

        task.setStatus(TaskStatus.TODO);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        emailService.sendEmail(
                user.getEmail(),
                "Task Created ✅",
                "Task '" + task.getTitle() + "' has been created."
        );

        return savedTask;
    }

    @Cacheable(
            value = "userTasks",
            key = "#username + '_' + #pageable.pageNumber + '_' + #pageable.pageSize"
    )
    public List<TaskResponseDto> getUserTasks(String username, Pageable pageable) {

        System.out.println("🔥 DB HIT");

        Page<Task> page = taskRepository.findByUserUsername(username, pageable);

        return page.getContent().stream()
                .map(task -> TaskResponseDto.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .priority(task.getPriority())
                        .status(task.getStatus())
                        .dueDate(task.getDueDate())
                        .build())
                .toList();
    }
    @CacheEvict(value = "userTasks", allEntries = true)
    public Task updateTask(Long id, Task updated, String username) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to update this task");
        }

        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setPriority(updated.getPriority());
        task.setStatus(updated.getStatus());
        task.setDueDate(updated.getDueDate());

        return taskRepository.save(task);
    }

    @CacheEvict(value = "userTasks", allEntries = true)
    public void deleteTask(Long id, String username) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }

    public void markOverdueTasks() {
        List<Task> tasks = taskRepository.findAll();

        tasks.stream()
                .filter(task ->
                        task.getDueDate().isBefore(LocalDateTime.now()) &&
                                task.getStatus() != TaskStatus.DONE &&
                                task.getStatus() != TaskStatus.DELAYED
                )
                .forEach(task -> {
                    task.setStatus(TaskStatus.DELAYED);

                    emailService.sendEmail(
                            task.getUser().getEmail(),
                            "Task Overdue ⚠️",
                            "Your task '" + task.getTitle() + "' is overdue."
                    );
                });

        taskRepository.saveAll(tasks);
    }
}