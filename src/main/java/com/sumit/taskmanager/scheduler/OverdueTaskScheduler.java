package com.sumit.taskmanager.scheduler;

import com.sumit.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverdueTaskScheduler {

    private final TaskService taskService;

    @Scheduled(fixedRate = 60000)
    public void run() {
        System.out.println("Scheduler running...");
        taskService.markOverdueTasks();
    }
}