package com.minProject.root.repository;

import com.minProject.root.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Long> {

    @Query("SELECT t FROM Teacher t WHERE t.email = :email AND t.password = :password")
    Optional<Teacher> isTeacherVerified(@Param("email") String email,
                                        @Param("password") String password);

    @Query("SELECT t FROM Teacher t WHERE t.email = :email")
    Optional<Teacher> isEmailExists(@Param("email") String email);

    @Query("SELECT t FROM Teacher t WHERE t.email = :email")
    Teacher GetByEmail(@Param("email") String email);

}
