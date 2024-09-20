package com.dbdevdeep.approve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dbdevdeep.approve.domain.ApproDraft;

@Repository
public interface ApproDraftRepository extends JpaRepository<ApproDraft, Long>{

}
