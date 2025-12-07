package com.minProject.root.repository;

import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizesRepository extends JpaRepository<Quizes,Long> {
    @Query("SELECT q FROM Quizes q WHERE q.url = :url")
    Quizes findByUrl(@Param("url") String url);
}
