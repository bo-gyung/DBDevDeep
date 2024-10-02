package com.dbdevdeep.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dbdevdeep.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long>{

	//place_status가 'Y'인 장소조회하기
	List<Place> findByPlaceStatus(String placeStatus);
	
	
	// (장소)가장큰 번호로 접근
	@Query("SELECT MAX(p.placeNo) FROM Place p")
	Long findMaxPlaceNo();
	
	// 특정 장소 번호로 장소 조회
	Place findByplaceNo(Long place_no);
	

    // unuseable_start_date에서 연도 추출
    @Query("SELECT SUBSTRING(p.unuseableStartDate, 1, 10) FROM Place p")
    List<String> findFormattedUnuseableStartDate();
    
 // unuseable_end_date에서 연도 추출
    @Query("SELECT SUBSTRING(p.unuseableEndDate, 1, 10) FROM Place p")
    List<String> findFormattedUnuseableEndDate();

    // 사용불가 시작일 null 아닌거
	@Query("SELECT p FROM Place p WHERE p.unuseableStartDate IS NOT NULL")
	List<Place> findAllWithUnuseableStartDate();
	// 사용불가 종료일 null 아닌거
	@Query("SELECT p FROM Place p WHERE p.unuseableEndDate IS NOT NULL")
	List<Place> findAllWithUnuseableEndDate();
}
	