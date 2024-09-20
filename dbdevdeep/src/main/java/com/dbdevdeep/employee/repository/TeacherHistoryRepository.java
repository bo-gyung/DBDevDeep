package com.dbdevdeep.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.vo.GradeClassGroup;

public interface TeacherHistoryRepository extends JpaRepository<TeacherHistory, Long>{
	
	@Query("SELECT COUNT(t) FROM TeacherHistory t WHERE t.tYear = :tYear")
	int findByTYearForCreateClass(@Param("tYear") String tYear);
	
	@Query("SELECT t FROM TeacherHistory t WHERE t.tYear = :tYear")
	List<TeacherHistory> findByClassByYear(@Param("tYear") String tYear);
	
	@Query("SELECT t.tYear FROM TeacherHistory t GROUP BY t.tYear ORDER BY t.tYear DESC LIMIT 1")
	String findMostRecentYear();
	
	@Query("SELECT t FROM TeacherHistory t WHERE t.tYear = :tYear AND t.employee.empId = :empId")
	TeacherHistory selectHistoryOne(@Param("tYear") String tYear, @Param("empId") String empId);
	
	@Query("DELETE FROM TeacherHistory t WHERE t.tYear = : tYear")
	void deleteByTYear(@Param("tYear") String tYear);
		
	@Query("SELECT t.teacherNo FROM TeacherHistory t WHERE t.tYear = :tYear AND t.grade = :grade AND t.gradeClass = :gradeClass")
	Long selectByGradeClassTyear(@Param("grade") int grade, @Param("gradeClass") int gradeClass, @Param("tYear") String tYear);
	
	@Query("SELECT COUNT(t) FROM TeacherHistory t WHERE t.tYear = :tYear AND t.grade = :grade")
	int countByTyearGrade(@Param("tYear") String tYear, @Param("grade") int grade);
	
	@Query("SELECT t FROM FROM TeacherHistory t WHERE t.employee.empId = :empId")
	List<TeacherHistory> selectTeacherHistoryByEmployee(@Param("empId") String empId);

	@Query("SELECT t FROM TeacherHistory t WHERE t.tYear = :tYear AND (:grade IS NULL OR t.grade = :grade)")
	List<TeacherHistory> findByYearAndGrade(@Param("tYear") String tYear, @Param("grade") String grade);
	
	TeacherHistory findByteacherNo(Long teacher_no);
	
	@Query("SELECT t FROM TeacherHistory t WHERE t.employee.empId = :empId AND t.tYear = (SELECT MAX(th.tYear) FROM TeacherHistory th WHERE th.employee.empId = :empId)")
	TeacherHistory selectLatestTeacherHistoryByEmployee(@Param("empId") String empId);
}
