package com.dbdevdeep.employee.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long>{
	
	@Query("SELECT COUNT(t) FROM Transfer t WHERE t.employee.empId = :empId AND t.transDate = :transDate AND t.transSchoolId = :transSchoolId AND t.transType = :transType")
	int findBySameTransfer(@Param("empId") String empId, @Param("transDate") LocalDate transDate, @Param("transSchoolId") String transSchoolId, @Param("transType") String transType);
	
	@Query("SELECT t FROM Transfer t WHERE t.employee.empId = :empId")
	List<Transfer> selectTransferHistoryByEmployee(@Param("empId") String empId);
	
	@Query("SELECT t FROM Transfer t JOIN t.employee e WHERE t.transDate < :yesterday AND e.entStatus = 'Y'")
	List<Transfer> findTransfersToProcess(@Param("yesterday") LocalDate yesterday);
}
