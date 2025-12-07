package com.minProject.root.repository;

import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    @Query("SELECT t FROM Student t WHERE t.email = :email AND t.password = :password")
    Optional<Student> isStudentVerified(@Param("email") String email,
                                        @Param("password") String password);

    @Query("Select t From Student t Where t.email = :email")
    Optional<Student> findByEmail(@Param("email") String email);
}
