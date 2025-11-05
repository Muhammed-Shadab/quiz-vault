package com.minProject.root.service;


import com.minProject.root.entity.Teacher;
import com.minProject.root.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepo;

    public boolean addTeacher(Teacher t){
        if(teacherRepo.isEmailExists(t.getEmail()).isPresent()) return false;
        teacherRepo.save(t);
        return true;
    }

    public boolean isTeacherVerified(Teacher t) {
        String email = t.getEmail();
        String password = t.getPassword();
        Optional<Teacher> res = teacherRepo.isTeacherVerified(email,password);
        if(!res.isPresent()) return false;
        return true;
    }
}
