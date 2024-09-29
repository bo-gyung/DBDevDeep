package com.dbdevdeep.place.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Mapper
public interface PlaceScheduleVoMapper {

	// 상세조회
	PlaceItemScheduleVo selectScheduleDetail(@Param("placeScheduleNo") Long placeScheduleNo);
	
	

	
	//전체 일정 조회
	List<PlaceItemScheduleVo> getTotalScheduleList();
}
