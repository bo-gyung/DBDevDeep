package com.dbdevdeep.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.notice.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
	
	Notice findBynoticeNo(Long notice_no);
	
	// 최신 등록 날짜 기준으로 상위 6개를 조회하는 메서드
    List<Notice> findTop6ByOrderByRegTimeDesc();
}
