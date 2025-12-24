package com.minProject.root.repository;

import com.minProject.root.entity.ActiveStatus;
import com.minProject.root.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ActiveStatusRepository extends JpaRepository<ActiveStatus, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE ActiveStatus a SET a.lastActiveTime = :lastEventDateTime WHERE a.student.id = :studentId")
    void updateActiveTime(Long studentId, LocalDateTime lastEventDateTime);


    @Query("SELECT a.lastActiveTime FROM ActiveStatus a WHERE a.student.id = :studentId")
    LocalDateTime isStudentACtive(@Param("studentId") long studentId);
}
