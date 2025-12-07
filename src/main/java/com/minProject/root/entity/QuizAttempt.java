package com.minProject.root.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long QAId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "QuizId")
    private Quizes quiz;

    private int correctQuestionsCount;
    private int score;

    private LocalDateTime start_time;
    private LocalDateTime end_time;

}
