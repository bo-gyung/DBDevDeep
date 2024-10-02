package com.dbdevdeep.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.place.domain.PlaceItemSchedule;

public interface PlaceScheduleRepository extends JpaRepository<PlaceItemSchedule, Long>{

	PlaceItemSchedule findByPlaceScheduleNo(Long placeScheduleNo);
	
}
