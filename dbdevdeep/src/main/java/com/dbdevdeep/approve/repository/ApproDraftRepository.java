package com.dbdevdeep.approve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dbdevdeep.approve.domain.ApproDraft;
import com.dbdevdeep.employee.domain.Employee;

@Repository
public interface ApproDraftRepository extends JpaRepository<ApproDraft, Long>{

	@Query("SELECT a FROM ApproDraft a WHERE a.employee = :employee")
	List<ApproDraft> findByDraftList(@Param("employee") Employee employee);
	
	@Query("SELECT a FROM ApproDraft a WHERE a.draftNo = :draftNo")
	ApproDraft findByDraftNo(@Param("draftNo") Long draftNo);
}
