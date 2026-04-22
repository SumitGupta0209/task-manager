package com.sumit.taskmanager.controller;

import com.sumit.taskmanager.dto.TaskResponseDto;
import com.sumit.taskmanager.model.Task;
import com.sumit.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Task create(@RequestBody Task task, Principal principal) {
        return taskService.createTask(task, principal.getName());
    }

    @GetMapping
    public List<TaskResponseDto> getTasks(
            Principal principal,
            @PageableDefault(size = 5, sort = "dueDate") Pageable pageable) {

        return taskService.getUserTasks(principal.getName(), pageable);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id,
                       @RequestBody Task task,
                       Principal principal) {
        return taskService.updateTask(id, task, principal.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        taskService.deleteTask(id, principal.getName());
    }
}