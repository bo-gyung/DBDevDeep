package com.dbdevdeep.student.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.student.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
	
	Subject findBySubjectNo(Long subjectNo);
	
	List<Subject> findByTeacherHistory(TeacherHistory teacherHistory);
	
	@Query("SELECT s FROM Subject s WHERE s.teacherHistory = :teacherHistory AND (" +
	           "EXISTS (SELECT 1 FROM Subject sub WHERE sub.teacherHistory = :teacherHistory AND sub.subjectSemester = '2') " +
	           "AND s.subjectSemester = '2' OR NOT EXISTS (SELECT 1 FROM Subject sub WHERE sub.teacherHistory = :teacherHistory AND sub.subjectSemester = '2'))")
	    List<Subject> findByTeacherHistoryWithSemesterCondition(@Param("teacherHistory") TeacherHistory teacherHistory);
}
