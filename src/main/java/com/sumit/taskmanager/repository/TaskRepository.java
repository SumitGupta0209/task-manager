package com.sumit.taskmanager.repository;




import com.sumit.taskmanager.model.Task;
import com.sumit.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime time, String status);
    Page<Task> findByUserUsername(String username, Pageable pageable);
}
