package com.minProject.root.repository;

import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    @Query("SELECT t FROM Student t WHERE t.email = :email AND t.password = :password")
    Student isStudentVerified(@Param("email") String email,
                                        @Param("password") String password);

    @Query("Select t From Student t Where t.email = :email")
    Student findByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query("UPDATE Student s SET s.lastHeartBeat = :currHeartBeat WHERE s.email = :email")
    void setHeatBeat(@Param("email") String email,@Param("currHeartBeat") LocalDateTime currHeartBeat);

    @Query("SELECT s.lastHeartBeat FROM Student s WHERE s.email = :email")
    LocalDateTime getLastHeartBeat(@Param("email") String email);
}
