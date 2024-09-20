package com.dbdevdeep.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.employee.domain.Employee;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

	@Query("SELECT a FROM Attendance a WHERE a.attendDate = :ld AND a.employee = :employee")
	Attendance findByTodayCheckTime(@Param("employee") Employee employee, @Param("ld") LocalDate ld);
	
	@Query("SELECT a FROM Attendance a WHERE a.attendDate = :now")
	
	List<Attendance> selectByToDayList(@Param("now") LocalDate now); 
}
