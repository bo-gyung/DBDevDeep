package com.dbdevdeep.approve.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dbdevdeep.approve.domain.TempEdit;

public interface TempEditRepository extends JpaRepository<TempEdit, Long>{

	@Query("SELECT t.tempNo , t.tempName FROM TempEdit t")
	List<Object[]> findTempNoAndTempName();
	
	@Query("SELECT t FROM TempEdit t WHERE t.tempNo = :tempNo")
	TempEdit findByTempNo(@Param("tempNo") Long tempNo);

}
