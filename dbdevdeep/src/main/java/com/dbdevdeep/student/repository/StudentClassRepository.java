package com.dbdevdeep.student.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dbdevdeep.student.domain.StudentClass;

public interface StudentClassRepository extends JpaRepository<StudentClass, Long>{
	List<StudentClass> findByStudent_StudentNo(Long studentNo);
	
	@Query("SELECT s, sc FROM Student s " +
		       "LEFT JOIN StudentClass sc ON s.studentNo = sc.student.studentNo " +
		       "AND (sc.teacherHistory.grade IS NULL OR sc.teacherHistory.grade = " +
		       "(SELECT MAX(th.grade) FROM StudentClass sc2 JOIN sc2.teacherHistory th WHERE sc2.student.studentNo = s.studentNo))")
	List<Object[]> findRecentYearAll();
}
