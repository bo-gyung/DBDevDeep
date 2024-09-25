package com.dbdevdeep.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.employee.domain.AuditLog;
import com.dbdevdeep.employee.domain.Employee;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{

	@Query("SELECT al FROM AuditLog al WHERE al.auditNo = :auditNo")
    AuditLog selectByAuditNoOne(@Param("auditNo") Long auditNo);
}
