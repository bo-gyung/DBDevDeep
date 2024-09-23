package com.dbdevdeep.place.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.TeacherHistoryRepository;
import com.dbdevdeep.place.domain.PlaceItemScheduleDto;
import com.dbdevdeep.place.mybatis.mapper.PlaceScheduleVoMapper;
import com.dbdevdeep.place.repository.ItemRepository;
import com.dbdevdeep.place.repository.PlaceRepository;

@Service
public class PlaceScheduleService {
	// MyBatis Mapper 인터페이스를 주입받기 위한 필드
	private final PlaceScheduleVoMapper placeScheduleVoMapper;
	
	// 필요 시 사용하는 다른 레포지토리 및 서비스
	private final EmployeeRepository employeeRepository;
	private final PlaceRepository placeRepository;
	private final ItemRepository itemRepository;
	private final TeacherHistoryRepository teacherHistoryRepository;
	
	@Autowired
	public PlaceScheduleService(PlaceScheduleVoMapper placeScheduleVoMapper,EmployeeRepository employeeRepository,
			PlaceRepository placeRepository, ItemRepository itemRepository,
			 TeacherHistoryRepository teacherHistoryRepository) {
		
		this.placeScheduleVoMapper = placeScheduleVoMapper;
		this.employeeRepository = employeeRepository;
		this.placeRepository = placeRepository;
		this.itemRepository = itemRepository;
		this.teacherHistoryRepository = teacherHistoryRepository;
	}

	
	// 전체일정 조회
	public List<PlaceItemScheduleDto> selectTotalScheduleList(){
		
		// Mapper를 통해 데이터베이스에서 모든 일정을 조회
        List<PlaceItemScheduleDto> totalSchedule = placeScheduleVoMapper.getTotalScheduleList();
        
        return totalSchedule; // 조회된 일정 리스트 반환
	}
}
