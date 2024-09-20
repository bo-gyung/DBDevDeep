package com.dbdevdeep.student.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.student.domain.TimeTable;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long>{
	List<TimeTable> findBySubject_SubjectNo(Long subjectNo);
}
