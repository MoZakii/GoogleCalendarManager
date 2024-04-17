package com.Management.TaskManagement.Controllers;

import com.Management.TaskManagement.Models.Task;
import com.Management.TaskManagement.Services.CalendarService;
import com.Management.TaskManagement.Services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;

@Controller
@RequestMapping("/tasks")
@Validated
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping()
    public String getAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/create-task")
    public String createTask() {
        return "create-task";
    }

    @PostMapping("/create-task")
    public String addTaskWeb(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/update-task/{id}")
    public String updateTaskWeb(Model model, @PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        return "update-task";
    }

    @PostMapping("/update-task/{id}")
    public String updateTaskWeb(@ModelAttribute Task task, @PathVariable Long id) {
        taskService.updateTask(id, task);
        return "redirect:/tasks";
    }





    @GetMapping("/api")
    public ResponseEntity<List<Task>> getTasks()
    {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/api/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id)
    {
        Task fetchedTask = taskService.getTaskById(id);
        if (fetchedTask != null)
            return ResponseEntity.ok(fetchedTask);
        else
            throw new EntityNotFoundException();
    }

    @PostMapping("/api")
    public ResponseEntity<Object> addTask(@Valid @RequestBody Task task)
    {
        task.setSubmitted(false);
        if(!taskService.validateDates(task.getStartDate(),task.getEndDate()))
            return ResponseEntity.badRequest().body("End date must be after start date");
        Task addedTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedTask);
    }

    @PutMapping("/api/{id}")
    public ResponseEntity<Object> updateTask(@Valid @RequestBody Task task, @PathVariable Long id)
    {
        if(!taskService.validateDates(task.getStartDate(),task.getEndDate()))
            return ResponseEntity.badRequest().body("End date must be after start date");
        Task updatedTask = taskService.updateTask(id, task);
        if (updatedTask != null)
        {
            return ResponseEntity.ok(updatedTask);
        }
        else
        {
            throw new EntityNotFoundException();
        }
    }

    @DeleteMapping("/api/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id)
    {
        boolean deleted = taskService.deleteTask(id);
        if(deleted)
            return ResponseEntity.ok().build();
        else
            throw new EntityNotFoundException();
    }

    @PostMapping("/api/submit")
    @Retryable(value = { UnknownHostException.class }, maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 1000 * 30))
    public ResponseEntity<Object> submitTasks() throws UnknownHostException {
        List<Task> tasks = taskService.getUnsubmittedTasks();
        if (tasks.isEmpty())
            return ResponseEntity.ok("There are no tasks to submit.");
        try
        {
            CalendarService.submitManyToCalendar(tasks);
            taskService.updateTasksWithSubmitted(tasks);
        }
        catch (UnknownHostException exception)
        {
            throw exception;
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok("You have submitted the tasks successfully");
    }
}
