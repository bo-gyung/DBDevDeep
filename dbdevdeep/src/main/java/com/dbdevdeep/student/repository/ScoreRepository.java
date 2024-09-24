package com.dbdevdeep.student.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.student.domain.Score;

public interface ScoreRepository extends JpaRepository<Score, Long>{
	List<Score> findByStudent_StudentNo(Long student_no);
	List<Score> findBySubject_SubjectNo(Long subject_no);
	
	Optional<Score> findByStudent_StudentNoAndCurriculum_CurriculumNo(Long student_no, Long curriculum_no);
}
