package com.dbdevdeep.student.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.student.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
	
	Subject findBySubjectNo(Long subjectNo);
	
	
}
