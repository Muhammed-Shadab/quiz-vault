package com.minProject.root.service;

import com.minProject.root.entity.Student;
import com.minProject.root.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    public void addStudent(Student stn){
        studentRepo.save(stn);
    }

}
