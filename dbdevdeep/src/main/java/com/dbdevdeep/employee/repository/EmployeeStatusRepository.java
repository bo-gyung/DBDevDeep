package com.dbdevdeep.employee.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.EmployeeStatus;

public interface EmployeeStatusRepository extends JpaRepository<EmployeeStatus, Long>{

	@Query("SELECT COUNT(es) FROM EmployeeStatus es WHERE es.employee.empId = :empId AND es.stopDate = :stopDate AND es.exceptedDate = :exceptedDate AND es.stopReason = :stopReason AND es.statusType = :statusType")
	int findBySameRest(@Param("empId") String empId, @Param("stopDate") LocalDate stopDate, @Param("exceptedDate") LocalDate exceptedDate, @Param("stopReason") String stopReason, @Param("statusType") String statusType);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.employee.empId = :empId AND es.statusType = 'R'")
	List<EmployeeStatus> selectRestHistoryByEmployee(@Param("empId") String empId);
	
	@Query("SELECT COUNT(es) FROM EmployeeStatus es WHERE es.employee.empId = :empId AND es.statusType = :statusType")
	int findBySameLeave(@Param("empId") String empId, @Param("statusType") String statusType);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.employee.empId = :empId AND es.statusType = 'N'")
	List<EmployeeStatus> selectLeaveHistoryByEmployee(@Param("empId") String empId);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.employee.empId = :empId AND es.statusType = 'R' AND es.returnDate IS NULL")
	EmployeeStatus findByReturnDateIsNull(@Param("empId") String empId);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.stopDate = :yesterday AND es.statusType = 'R'")
	List<EmployeeStatus> findRestsToProcess(@Param("yesterday") LocalDate yesterday);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.stopDate = :yesterday AND es.statusType = 'N'")
	List<EmployeeStatus> findLeavesToProcess(@Param("yesterday") LocalDate yesterday);
	
	@Query("SELECT es FROM EmployeeStatus es WHERE es.returnDate = :yesterday AND es.statusType = 'R'")
	List<EmployeeStatus> findReturnsToProcess(@Param("yesterday") LocalDate yesterday);
	
}
