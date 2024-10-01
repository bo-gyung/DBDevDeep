package com.dbdevdeep.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.employee.domain.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

	@Query("SELECT a FROM Attendance a WHERE a.attendDate = :ld AND a.employee = :employee")
	Attendance findByTodayCheckTime(@Param("employee") Employee employee, @Param("ld") LocalDate ld);
	
	@Query("SELECT a FROM Attendance a WHERE a.attendDate = :now AND workStatus = 1")
	List<Attendance> selectByToDayList(@Param("now") LocalDate now); 
	
	@Query("SELECT a FROM Attendance a WHERE a.attendDate = :now AND workStatus = 2")
	List<Attendance> selectByToDayListLeave(@Param("now") LocalDate now); 

	@Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND a.attendDate BETWEEN :startDate AND :endDate")
	List<Attendance> findByYearAndMonth(@Param("employee") Employee employee, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT a FROM Attendance a WHERE a.employee = :employee")
	Attendance findByEmpId(@Param("employee") Employee employee);
	
	@Query("SELECT MAX(a.overtimeSum) FROM Attendance a WHERE a.employee = :employee AND FUNCTION('YEAR', a.attendDate) = :year AND FUNCTION('MONTH', a.attendDate) = :month")
	Optional<Integer> findByLastInfo(@Param("employee") Employee employee , @Param("year") int year , @Param("month") int month);
	
	@Query("SELECT a FROM Attendance a WHERE a.employee = :employee")
	List<Attendance> findByEmpIdList(@Param("employee") Employee employee);
	
	Attendance findByattendNo(Long attend_no);
	
	@Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND a.attendDate = :attendDate")
	Attendance findByEmpAndDate(@Param("employee") Employee employee , @Param("attendDate") LocalDate attendDate);
}
