package com.dbdevdeep.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.place.domain.Item;


public interface ItemRepository extends JpaRepository<Item, Long>{

	Item findByitemNo(Long item_no);
	//장소번호를 기준으로 항목조회
	List<Item> findByPlacePlaceNo(Long placeNo);
	
	
	// 장소 번호와 일련번호로 중복 여부 확인
	boolean existsByPlacePlaceNoAndItemSerialNo(Long placeNo, String itemSerialNo);
}
