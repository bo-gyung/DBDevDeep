package com.dbdevdeep.place.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Mapper
public interface PlaceScheduleVoMapper {

	
	
	
	
	
	// 등록
	int insertPlaceSchedule(PlaceItemScheduleVo vo);
	
	//전체 일정 조회
	List<PlaceItemScheduleVo> getTotalScheduleList();
}
