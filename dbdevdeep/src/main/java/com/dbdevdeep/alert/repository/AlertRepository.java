package com.dbdevdeep.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.alert.domain.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long>{

}
