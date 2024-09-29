package com.dbdevdeep.place.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
	
	//일정 삭제
	  public int deletePlaceSchedule(Long place_schedule_no) {
		  try {
	            // 삭제 수행
	            placeScheduleRepository.deleteById(place_schedule_no);
	            return 1; // 성공적으로 삭제된 경우 1 반환
	        } catch (EmptyResultDataAccessException e) {
	            // 이미 존재하지 않는 경우 예외 처리
	            return 0; // 삭제할 일정이 존재하지 않는 경우 0 반환
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0; // 다른 오류 발생 시 0 반환
	        }
	    }
	
	
	
	// 일정상세조회
	public PlaceItemScheduleVo getScheduleDetail(Long placeScheduleNo) {
		
		PlaceItemSchedule pis = placeScheduleRepository.findByPlaceScheduleNo(placeScheduleNo);
		
		if(pis == null) {
			 throw new IllegalArgumentException("해당 일정이 존재하지 않습니다: " + pis);
		}
	
		PlaceItemScheduleVo pisv = PlaceItemScheduleVo.builder()
			.place_schedule_no(pis.getPlaceScheduleNo())
			.place_schedule_title(pis.getPlaceScheduleTitle())
			.place_no(pis.getPlace().getPlaceNo())
			.place_name(pis.getPlace().getPlaceName())
			.emp_id(pis.getEmployee().getEmpId())
			.emp_name(pis.getEmployee().getEmpName())
			.start_date(pis.getStartDate())
			.start_time(pis.getStartTime())
			.end_date(pis.getEndDate())
			.end_time(pis.getEndTime())
			.item_no(pis.getItem().getItemNo())
			.item_name(pis.getItem().getItemName())
			.item_quantity(pis.getItem().getItemQuantity())
			.place_schedule_content(pis.getPlaceScheduleContent())
			.build();
		
		return pisv;
		
	}
	
	
	// 일정 겹침 여부 확인하는 메소드
	public boolean isScheduleOverlapping(Long placeNo, String newStartDate, String newStartTime,
			String newEndDate, String newEndTime) {
		
		// place_no가 0인 경우 겹침 검사를 하지 않고 바로 false 반환
	    if (placeNo == 0) {
	        return false; // 운동장은 겹침 검사 없이 항상 사용 가능
	    }
		
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
	
	
	
	

	
	// 일정 등록 메소드
	public PlaceItemSchedule createPlaceSchedule(PlaceItemScheduleVo vo) {
	    try {
	        Place place = placeRepository.findByplaceNo(vo.getPlace_no());
	        if (place == null && vo.getPlace_no() != 0) {
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
	        StringBuilder serialNoBuilder = new StringBuilder();
	        
	        // 선언 위치를 for 루프 바깥으로 이동합니다.
	        Item item = null;
	        
	        // place_no가 0이 아닌 경우에만 item_no 검증수행
	        if(vo.getPlace_no() != 0) {
	        	
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
