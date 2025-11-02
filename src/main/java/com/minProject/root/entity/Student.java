package com.minProject.root.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private String name;
    private String email;
    private String phoneNo;
    private String gender;
    private String password;
    private String instituteName;
    private String fieldOfStudy;
    private int semester;
    private String division;
    private String rollNo;
    private String department;


}
