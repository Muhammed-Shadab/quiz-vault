package com.minProject.root.service;

import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepo;

    public void addStudent(Student stn){
        studentRepo.save(stn);
    }

    public boolean isStudentVerified(Student st) {
        String email = st.getEmail();
        String password = st.getPassword();
        Optional<Student> res = studentRepo.isStudentVerified(email,password);
        if(!res.isPresent()) return false;
        return true;
    }
}
