package com.minProject.root.repository;

import com.minProject.root.entity.Rooms;
import com.minProject.root.entity.Student;
import com.minProject.root.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface RoomsRepository extends JpaRepository<Rooms,Long> {
    @Query("SELECT r.roomName FROM Rooms r WHERE r.teacherId.id = :teacherId GROUP BY r.roomName")
    List<String> getGroupedRoomNames(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(r) FROM Rooms r WHERE r.roomName = :roomName")
    int CountStudentByRooms(@Param("roomName") String roomName);

    @Query("SELECT r.roomName From Rooms r WHERE r.studentId.id = :studentId GROUP BY r.roomName,r.studentId.id")
    List<String> findRoomsByStudent(Long studentId);

    @Query("SELECT r FROM Rooms r WHERE r.studentId.id = :studentId AND r.roomName = :roomName")
    Rooms findByRoomAndStudent(@Param("roomName") String roomName,@Param("studentId") Long studentId);

    @Query("SELECT s FROM Student s WHERE s.studentId IN ( SELECT r.studentId.id FROM Rooms r WHERE r.roomName = :roomName AND r.teacherId.id = :teacherId)")
    List<Student> findStudentByRoomNameAndTeacher(@Param("roomName") String roomName,@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(r) FROM Rooms r WHERE r.teacherId.id = :teacherId")
    long countStudentsWithTeachers(@Param("teacherId") long teacherId);
}
