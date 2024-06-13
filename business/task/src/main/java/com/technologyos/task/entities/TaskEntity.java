package com.technologyos.task.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name="Task")
@Table(name = "tasks")
@Data
public class TaskEntity {

    @Id
    @SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_sequence")
    @Column(name = "task_id", updatable = false)
    private Long id;

    @Column(name = "task_name", nullable = false)
    private String name;
}
