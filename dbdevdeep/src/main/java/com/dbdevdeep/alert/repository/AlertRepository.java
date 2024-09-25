package com.dbdevdeep.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.alert.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long>{

	@Query("SELECT a FROM Alert a WHERE a.employee.empId = :empId AND a.alarmStatus = 'N'")
	List<Alert> findByempId(@Param("empId") String empId);
	
	@Query("SELECT a FROM Alert a WHERE a.referenceName = :referenceName AND a.referenceNo = :referenceNo")
	List<Alert> findByreferenceNameandreferenceNo(@Param("referenceName")String referenceName, @Param("referenceNo")Long referenceNo);
}
