package com.Management.TaskManagement.Services;

import com.Management.TaskManagement.Models.Task;
import com.Management.TaskManagement.Repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;


    @Cacheable("tasks")
    public List<Task> getAllTasks()
    {
        return taskRepository.findAll();
    }

    public List<Task> getUnsubmittedTasks()
    {
        return taskRepository.findByisSubmittedFalse();
    }

    @Cacheable("tasks")
    public Task getTaskById(Long id)
    {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent())
        {
            return task.get();
        }
        return null;
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task)
    {
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long id, Task updatedTask)
    {
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent())
        {
            updatedTask.setId(id);
            return taskRepository.save(updatedTask);
        }
        return null;
    }

    public boolean validateDates(LocalDateTime start, LocalDateTime end)
    {
        return start.isBefore(end) || start.isEqual(end);
    }
    public void updateTasksWithSubmitted(List<Task> tasks)
    {
        for (Task task : tasks) {
            task.setSubmitted(true);
        }
        taskRepository.saveAll(tasks);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public boolean deleteTask(Long id)
    {
        boolean exists = taskRepository.existsById(id);
        if(exists)
        {
            taskRepository.deleteById(id);
        }
        return exists;
    }

}
