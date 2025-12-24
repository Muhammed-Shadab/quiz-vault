package com.minProject.root.repository;

import com.minProject.root.entity.Announcement;
import com.minProject.root.entity.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface AnnouncementStatusRepository extends JpaRepository<AnnouncementStatus,Long> {

    @Query("SELECT a FROM Announcement a WHERE a.AnnId IN " +
            "(SELECT ast.announcement.AnnId FROM AnnouncementStatus ast WHERE ast.student.id = :studentId)")
    List<Announcement> findAnnouncementsForSpecificStudent(@Param("studentId") Long studentId);

}
