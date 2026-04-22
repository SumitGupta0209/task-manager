package com.sumit.taskmanager.dto;

import com.sumit.taskmanager.eums.Priority;
import com.sumit.taskmanager.eums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;
}