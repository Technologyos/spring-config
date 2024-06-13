package com.technologyos.task.controller;

import com.technologyos.task.entities.TaskEntity;
import com.technologyos.task.repository.TaskRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private Environment env;

    @GetMapping()
    @Operation(summary = "Get all tasks", description = "Get all tasks with active status sorted by ASC creating time", tags = {"Task"})
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Success", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = TaskEntity.class))
            }),
    @ApiResponse(responseCode = "400", description = "Bad request")})
    @CircuitBreaker(name="getTaskCB", fallbackMethod = "getTaskCFB")
    public ResponseEntity<List<TaskEntity>> findAll() {
        return Optional.ofNullable(taskRepository.findAll().isEmpty() ? null:taskRepository.findAll())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable(name = "taskId") Long taskId) {
        return Optional.ofNullable(taskRepository.findById(taskId).isEmpty() ? null : taskRepository.findById(taskId))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTaskById(@PathVariable(name = "taskId") long taskId, @RequestBody TaskEntity task) {
        TaskEntity currentTask = taskRepository.findById(taskId).get();
        currentTask.setName(task.getName());
        TaskEntity save = taskRepository.save(currentTask);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskEntity task) {
        TaskEntity taskEntity = taskRepository.save(task);
        return ResponseEntity.ok(taskEntity);
    }

    public void getTaskCFB(Throwable throwable){
        log.error("getTaskCFB error " + throwable);
    }

    @GetMapping("/check")
    public String check(){
        return "Your environment is " + env.getProperty("environment") + " with the database "+ env.getProperty("db");
    }
}
