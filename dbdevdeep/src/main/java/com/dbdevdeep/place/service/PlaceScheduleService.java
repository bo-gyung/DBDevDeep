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
	
	// 일정 겹침 여부 확인하는 메소드
	public boolean isScheduleOverlapping(Long placeNo, String newStartDate, String newStartTime,
			String newEndDate, String newEndTime) {
		
		List<PlaceItemScheduleVo> existingSchedules = placeScheduleVoMapper.getTotalScheduleList();
		
		// 일정 겹침 검사
		for(PlaceItemScheduleVo schedule : existingSchedules) {
			if(schedule.getPlace_no().equals(placeNo)) {
				String existingStartDateTime = schedule.getStart_date() + " " + schedule.getStart_time();
                String existingEndDateTime = schedule.getEnd_date() + " " + schedule.getEnd_time();
                String newStartDateTime = newStartDate + " " + newStartTime;
                String newEndDateTime = newEndDate + " " + newEndTime;
                
                // 기존 일정과 새 일정의 겹침 여부를 검사
                if (!(newEndDateTime.compareTo(existingStartDateTime) <= 0 || newStartDateTime.compareTo(existingEndDateTime) >= 0)) {
                    return true; // 겹침이 발생할 경우 true 반환
                }
			}
		}
		return false;
	}
	
	private String generateManagementNo(Long placeNo, String itemSerialNo) {
	    // 장소 번호와 단일 기자재 일련번호를 결합하여 관리번호 생성 (예: 01-A01)
	    return placeNo + "-" + itemSerialNo;
	}
	
	

	
	// 일정 등록 메소드
	public PlaceItemSchedule createPlaceSchedule(PlaceItemScheduleVo vo) {
	    try {
	        Place place = placeRepository.findByplaceNo(vo.getPlace_no());
	        if (place == null) {
	            throw new IllegalArgumentException("해당 장소를 찾을 수 없습니다: " + vo.getPlace_no());
	        }

	        Employee employee = employeeRepository.findByempId(vo.getEmp_id());
	        if (employee == null) {
	            throw new IllegalArgumentException("해당 신청인을 찾을 수 없습니다: " + vo.getEmp_id());
	        }

	        TeacherHistory teacherHistory = teacherHistoryRepository.findByteacherNo(vo.getTeacher_no());
	        if (teacherHistory == null) {
	            throw new IllegalArgumentException("해당 교사를 찾을 수 없습니다: " + vo.getTeacher_no());
	        }

	        // 문자열 빌더를 사용해 아이템 번호와 관리 번호를 결합
	        StringBuilder itemNoBuilder = new StringBuilder();
	        StringBuilder serialNoBuilder = new StringBuilder();
	        
	        // 선언 위치를 for 루프 바깥으로 이동합니다.
	        Item item = null;

	        for (Long itemNo : vo.getItemNoList()) {
	            item = itemRepository.findByitemNo(itemNo);
	            if (item == null) {
	                throw new IllegalArgumentException("해당 기자재를 찾을 수 없습니다: " + itemNo);
	            }

	            // 아이템 일련번호를 ','로 결합하여 management_no 생성에 사용
	            if (serialNoBuilder.length() > 0) {
	                serialNoBuilder.append(",");
	            }
	            serialNoBuilder.append(item.getItemSerialNo());
	        }

	        String managementNo = vo.getPlace_no() + "-" + serialNoBuilder.toString(); // 예: "01-A01,A02,A03"

	        // PlaceItemSchedule 객체 생성
	        PlaceItemSchedule placeItemSchedule = PlaceItemSchedule.builder()
	                .employee(employee)
	                .place(place)
	                .item(item) // 이 부분은 이제 for 루프 밖에서 선언된 item을 사용합니다.
	                .teacherHistory(teacherHistory)
	                .placeScheduleTitle(vo.getPlace_schedule_title())
	                .placeScheduleContent(vo.getPlace_schedule_content())
	                .startDate(vo.getStart_date())
	                .endDate(vo.getEnd_date())
	                .startTime(vo.getStart_time())
	                .endTime(vo.getEnd_time())
	                .managementNo(managementNo) // 결합된 관리 번호 저장
	                .regDate(vo.getReg_date())
	                .modDate(vo.getMod_date())
	                .build();

	        // 데이터베이스에 저장
	        return placeScheduleRepository.save(placeItemSchedule);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
    }
    
    
	    




	
    
    

    // 전체 일정 조회 (VO 사용)
    public List<PlaceItemScheduleVo> selectTotalScheduleList() {
        // Mapper를 통해 데이터베이스에서 모든 일정을 조회 (VO로 바로 반환)
        return placeScheduleVoMapper.getTotalScheduleList();
    }
}
