package com.dbdevdeep.student.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.student.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
	
	Subject findBySubjectNo(Long subjectNo);
	
	List<Subject> findByTeacherHistory(TeacherHistory teacherHistory);
}
