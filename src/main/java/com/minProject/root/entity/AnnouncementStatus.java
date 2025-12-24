package com.minProject.root.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class AnnouncementStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ASId;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "AnnId")
    private Announcement announcement;

    private Boolean status;


}
