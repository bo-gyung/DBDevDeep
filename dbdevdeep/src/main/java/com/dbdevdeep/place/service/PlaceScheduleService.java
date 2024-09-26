package com.dbdevdeep.place.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.TeacherHistoryRepository;
import com.dbdevdeep.place.domain.Item;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.domain.PlaceItemSchedule;
import com.dbdevdeep.place.mybatis.mapper.PlaceScheduleVoMapper;
import com.dbdevdeep.place.repository.ItemRepository;
import com.dbdevdeep.place.repository.PlaceRepository;
import com.dbdevdeep.place.repository.PlaceScheduleRepository;
import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Service
public class PlaceScheduleService {
	// MyBatis Mapper 인터페이스를 주입받기 위한 필드
	private final PlaceScheduleVoMapper placeScheduleVoMapper;
	
	// 필요 시 사용하는 다른 레포지토리 및 서비스
	private final EmployeeRepository employeeRepository;
	private final PlaceRepository placeRepository;
	private final ItemRepository itemRepository;
	private final TeacherHistoryRepository teacherHistoryRepository;
	private final PlaceScheduleRepository placeScheduleRepository;
	@Autowired
	public PlaceScheduleService(PlaceScheduleVoMapper placeScheduleVoMapper,EmployeeRepository employeeRepository,
			PlaceRepository placeRepository, ItemRepository itemRepository,
			 TeacherHistoryRepository teacherHistoryRepository, PlaceScheduleRepository placeScheduleRepository) {
		
		this.placeScheduleVoMapper = placeScheduleVoMapper;
		this.employeeRepository = employeeRepository;
		this.placeRepository = placeRepository;
		this.itemRepository = itemRepository;
		this.teacherHistoryRepository = teacherHistoryRepository;
		this.placeScheduleRepository = placeScheduleRepository;
	}

	
	private String generateManagementNo(Long placeNo, String itemSerialNo) {
	    // 장소 번호와 단일 기자재 일련번호를 결합하여 관리번호 생성 (예: 01-A01)
	    return placeNo + "-" + itemSerialNo;
	}
	
	// 일정 등록
    public PlaceItemSchedule createPlaceSchedule(PlaceItemScheduleVo vo) {
        try {
            // 필요한 엔티티 조회
            Place place = placeRepository.findByplaceNo(vo.getPlace_no());
            Item item = itemRepository.findByitemNo(vo.getItem_no());
            Employee employee = employeeRepository.findByempId(vo.getEmp_id());
            TeacherHistory teacherHistory = teacherHistoryRepository.findByteacherNo(vo.getTeacher_no());
            
            
            if (place == null) {
                throw new IllegalArgumentException("해당 장소를 찾을 수 없습니다: " + vo.getPlace_no());
            }
           
            
            
         // Management No 생성: place_no와 item_serial_no를 결합하여 생성
            String managementNo = generateManagementNo(place.getPlaceNo(), item.getItemSerialNo());
            
            // PlaceItemSchedule 엔티티 생성
            PlaceItemSchedule placeItemSchedule = PlaceItemSchedule.builder()
                    .employee(employee)
                    .teacherHistory(teacherHistory)
                    .item(item)
                    .place(place)
                    .placeScheduleTitle(vo.getPlace_schedule_title())
                    .placeScheduleContent(vo.getPlace_schedule_content())
                    .startDate(vo.getStart_date())
                    .endDate(vo.getEnd_date())
                    .startTime(vo.getStart_time())
                    .endTime(vo.getEnd_time())
                    .managementNo(managementNo)  // 생성된 management_no 설정
                    .build();

            // 저장
            return placeScheduleRepository.save(placeItemSchedule);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    

    // 전체 일정 조회 (VO 사용)
    public List<PlaceItemScheduleVo> selectTotalScheduleList() {
        // Mapper를 통해 데이터베이스에서 모든 일정을 조회 (VO로 바로 반환)
        return placeScheduleVoMapper.getTotalScheduleList();
    }
}
