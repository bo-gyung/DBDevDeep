package com.dbdevdeep.approve.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.employee.domain.Employee;

public interface ApproveRepository extends JpaRepository<Approve, Long> {

	// 결재요청 휴가 목록 조회
	@Query("SELECT a FROM Approve a LEFT JOIN a.vacationRequests v WHERE a.approType = 0 AND a.employee.empId = :empId")
	List<Approve> findByTypeAndEmpId(@Param("empId") String empId);
	
	// 결재요청 보고서 목록 조회
	@Query("SELECT a FROM Approve a WHERE a.approType = 1 AND a.employee.empId = :empId ")
	List<Approve> findByAnotherTypeAndEmpId(@Param("empId") String empId);
	
	// 완료한 보고서 목록 조회
	@Query("SELECT a FROM Approve a WHERE a.approType = 1 AND a.approStatus = 1 ")
	List<Approve> findCompleteDocu();
	
	// 결재 요청받은 쿼리
	@Query(value = "SELECT a.appro_no AS approNo, a.appro_title AS approTitle, a.appro_time AS approTime, " +
            "a.appro_name AS approName, a.appro_status AS approStatus, " +
            "vr.vac_type AS vacType " +
            "FROM approve a " +
            "JOIN approve_line al ON a.appro_no = al.appro_no " +
            "LEFT JOIN vacation_request vr ON a.appro_no = vr.appro_no " +
            "WHERE al.emp_id = :loggedInUserEmpId " +
            "AND al.appro_line_status IN (1, 2, 3) " +
            "AND a.appro_type = 0", 
    nativeQuery = true)
	List<Object[]> findApprovalRequestsForUser(@Param("loggedInUserEmpId") String loggedInUserEmpId);
	
	Approve findByApproNo(Long approNo);
	
	// 참조 요청받은 쿼리
	@Query("SELECT a.approNo, a.approTitle, a.approTime, a.approName, a.approStatus, vr.vacType " +
		       "FROM Approve a " +
		       "LEFT JOIN a.vacationRequests vr " + 
		       "LEFT JOIN a.references r " +       
		       "WHERE r.employee.empId = :empId")
		List<Object[]> refRequests(@Param("empId") String empId);
		
	// 요청 받은 보고서 목록
	@Query("SELECT a.approNo, a.approTitle, a.approTime, a.approName, a.approStatus " +
		       "FROM Approve a " +
		       "LEFT JOIN a.approveLines al " +
		       "WHERE al.employee.empId = :empId AND al.approLineStatus IN (1, 2, 3) AND a.approType = 1")
	List<Object[]> findDocuRequestsForUser(@Param("empId") String empId);
	
	// 요청한 휴가 결재 카운트
	@Query("SELECT COUNT(a) FROM Approve a WHERE a.employee = :employee AND a.approType = 0 AND approStatus = 0")
	int findRequestApproveCountByEmpId(@Param("employee") Employee employee);
	
	// 요청한 보고서 결재 카운트
	@Query("SELECT COUNT(a) FROM Approve a WHERE a.employee = :employee AND a.approType = 1 AND approStatus = 0")
	int findRequestDocuByEmpId(@Param("employee") Employee employee);
	
	// 요청 받은 휴가 카운트
	@Query("SELECT COUNT(a) " +
		       "FROM Approve a " +
		       "LEFT JOIN a.approveLines al " +
		       "WHERE al.employee = :employee AND al.approLineStatus = 1 AND a.approType = 0")
	int findComeApproveCountByEmpId(@Param("employee") Employee employee);
	
	// 요청 받은 보고서 카운트
	@Query("SELECT COUNT(a) " +
		       "FROM Approve a " +
		       "LEFT JOIN a.approveLines al " +
		       "WHERE al.employee = :employee AND al.approLineStatus = 1 AND a.approType = 1")
	int findComeDocuByEmpId(@Param("employee") Employee employee);

}
