package com.minProject.root.repository;

import com.minProject.root.entity.QuizAttempt;
import com.minProject.root.entity.Quizes;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt,Long> {
    @Query("Select q FROM QuizAttempt q WHERE q.studentId.id = :studentId")
    List<QuizAttempt> findAllAttendedQuiz(@Param("studentId") Long studentId);


    @Query("SELECT COUNT(q) FROM QuizAttempt q WHERE q.quizId.id = :quizId")
    Long countTheAttendedQuizes(@Param("quizId") Long quizId);

    @Query("SELECT q FROM QuizAttempt q WHERE q.quizId.id = :quizId")
    List<QuizAttempt> findTheAttendedQuizes(@Param("quizId") Long quizId);

    @Query("SELECT q FROM QuizAttempt q WHERE q.studentId.id = :studentId AND q.quizId.id = :quizId")
    QuizAttempt isQuizAttemptedByStudent(@Param("quizId") Long quizId,@Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE QuizAttempt q SET q.correctQuestionsCount = :correctQuestionsCount,q.score = :score, q.end_time = :time WHERE q.QAId = :Id")
    void setscore(int correctQuestionsCount, int score, LocalDateTime time, long Id);


    @Modifying
    @Transactional
    @Query("UPDATE QuizAttempt q SET q.tabSwitchingCount = q.tabSwitchingCount + 1 WHERE q.QAId = :quizId AND q.end_time IS NULL")
    int updateTabSwitching(@Param("quizId") Long quizId);

    @Query("SELECT q.tabSwitchingCount FROM QuizAttempt q WHERE q.QAId = :quizId")
    Integer findTabSwitchingCount(@Param("quizId") Long quizId);

    @Query("SELECT q FROM QuizAttempt q WHERE q.QAId = :qaId")
    QuizAttempt FindById(Long qaId);
}
