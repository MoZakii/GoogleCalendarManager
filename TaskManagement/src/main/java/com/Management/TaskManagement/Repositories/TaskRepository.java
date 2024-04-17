package com.Management.TaskManagement.Repositories;

import com.Management.TaskManagement.Models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByisSubmittedFalse();
}
