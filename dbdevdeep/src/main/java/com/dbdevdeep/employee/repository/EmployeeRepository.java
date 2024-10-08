package com.dbdevdeep.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String>{
	Employee findByempId(String emp_id);
	
	Employee findBygovId(String gov_id);
	
	@Query("SELECT e FROM Employee e WHERE e.entStatus = 'Y'")
	List<Employee> selectYEmployeeList();
	
	@Query("SELECT SUBSTR(e.empId, 5) FROM Employee e WHERE e.empId LIKE CONCAT(:year, '%') ORDER BY e.empId DESC LIMIT 1")
	String findByempIdWhen(@Param("year") String year);
	
	@Modifying
	@Query("UPDATE Employee e SET e.loginYn = :loginYn WHERE e.empId = :empId")
	int updateByEmpidToLoginyn(@Param("empId") String empId, @Param("loginYn") String loginYn);
	
	@Modifying
	@Query("UPDATE Employee e SET e.chatStatusMsg = :chatStatusMsg WHERE e.empId = :empId")
    int updateByEmpIdToStatus(@Param("empId") String empId, @Param("chatStatusMsg") String chatStatusMsg);
}
