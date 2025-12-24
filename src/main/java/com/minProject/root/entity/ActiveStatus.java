package com.minProject.root.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class ActiveStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long acId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private LocalDateTime lastActiveTime;
}
