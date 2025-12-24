package com.minProject.root.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Announcement{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AnnId;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    private String roomName;

    private String title;
    private String message;

    private LocalDateTime createdAt;


}
