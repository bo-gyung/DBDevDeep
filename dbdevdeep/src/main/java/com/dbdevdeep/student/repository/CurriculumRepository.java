package com.dbdevdeep.student.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.student.domain.Curriculum;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long>{

	List<Curriculum> findBySubject_SubjectNo(Long subjectNo);
	Curriculum findBycurriculumNo(Long curriculum_no);
}
