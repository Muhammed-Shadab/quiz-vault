package com.minProject.root.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Quizes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    private String title;
    private String description;
    private int questionsCount;
    private int marksOfEachQuestion;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime ExpireAt;

    @ManyToOne
    @JoinColumn(name = "teacherId")  // foreign key column
    private Teacher teacherId;
}
