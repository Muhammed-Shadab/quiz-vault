package com.minProject.root.repository;

import com.minProject.root.entity.Quizes;
import com.minProject.root.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizesRepository extends JpaRepository<Quizes,Long> {
    @Query("SELECT q FROM Quizes q WHERE q.url = :url")
    Quizes findByUrl(@Param("url") String url);

    @Query("SELECT q FROM Quizes q WHERE q.teacherId.id = :teacherId")
    List<Quizes> findTheQuizesByTeacherId(@Param("teacherId") Long teacherId);

    @Query("Select count(q) FROM Quizes q WHERE q.roomName = :roomName AND q.teacherId.id = :teacherId")
    int countQuizesByRooms(String roomName, Long teacherId);

    @Query("SELECT q FROM Quizes q WHERE q.roomName = :roomName AND q.expireAt > CURRENT_TIMESTAMP")
    List<Quizes> findQuizByRoomName(@Param("roomName") String roomName);

    @Query("SELECT q FROM Quizes q WHERE q.roomName = :roomName")
    List<Quizes> findAllQuizByRoomName(@Param("roomName") String roomName);

    @Query("SELECT q FROM Quizes q WHERE q.roomName = :roomName AND q.title = :title")
    Quizes findTheQuizByRoomNameAndTitle(@Param("roomName") String roomName,
                                         @Param("title") String title);
}
