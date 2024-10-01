package com.dbdevdeep.place.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	
	
	// 일정현황리스트
	public List<PlaceItemScheduleVo> scheduleList() {
	    List<PlaceItemScheduleVo> totalSchedule = placeScheduleVoMapper.getTotalScheduleList();
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	    for (PlaceItemScheduleVo schedule : totalSchedule) {
	        if (schedule.getReg_date() != null) {
	            String formattedDate = schedule.getReg_date().format(dateFormatter);
	            schedule.setFormattedRegDate(formattedDate);
	            
	        }
	        // 학년/반 정보 설정 및 제목 구성
	        String formattedTitle = schedule.getPlace_schedule_title(); // 기본 제목 설정

	        if (schedule.getEmp_id() != null) {
	            TeacherHistory latestHistory = teacherHistoryRepository.selectLatestTeacherHistoryByEmployee(schedule.getEmp_id());

	            if (latestHistory != null && latestHistory.getGrade() > 0 && latestHistory.getGradeClass() > 0) {
	                // grade와 gradeClass가 유효한 경우만 학년/반을 추가
	                formattedTitle = latestHistory.getGrade() + "학년 " + latestHistory.getGradeClass() + "반 - " + schedule.getPlace_schedule_title();
	            }
	        }
	        
	        // 구성된 제목을 VO에 설정
	        schedule.setPlace_schedule_title(formattedTitle);
	    }

	    return totalSchedule;
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
	
	
	// 일정 상세조회
	  public PlaceItemScheduleVo getScheduleDetail(Long placeScheduleNo) {
		    PlaceItemSchedule pis = placeScheduleRepository.findByPlaceScheduleNo(placeScheduleNo);

		    if (pis == null) {
		        throw new IllegalArgumentException("해당 일정이 존재하지 않습니다: " + placeScheduleNo);
		    }

		    // place와 item, employee가 null이 아닌지 확인
		    Place place = pis.getPlace();
		    Item item = pis.getItem();
		    Employee employee = pis.getEmployee();

		    if (place == null) {
		        throw new IllegalStateException("일정에 해당하는 장소 정보가 없습니다.");
		    }

		    if (employee == null) {
		        throw new IllegalStateException("일정에 해당하는 신청자 정보가 없습니다.");
		    }

		    // place_no가 0인 경우에만 item 관련 처리
		    Long itemNo = null;
		    String itemName = null;
		    Integer itemQuantity = null;

		    if (place.getPlaceNo() == 0) {
		        // item이 null일 수 있으므로 null 체크
		        if (item != null) {
		            itemNo = item.getItemNo();
		            itemName = item.getItemName();
		            itemQuantity = item.getItemQuantity();
		        } else {
		            itemName = "전체";
		            itemQuantity = 0;
		        }
		    } else {
		        // item이 null일 수 있으므로 null 체크
		        if (item != null) {
		            itemNo = item.getItemNo();
		            itemName = item.getItemName();
		            itemQuantity = item.getItemQuantity();
		        } else {
		            // 필요한 경우 기본값 설정 또는 예외 처리
		            itemName = "없음";
		            itemQuantity = 0;
		        }
		    }

		    PlaceItemScheduleVo pisv = PlaceItemScheduleVo.builder()
		        .place_schedule_no(pis.getPlaceScheduleNo())
		        .place_schedule_title(pis.getPlaceScheduleTitle())
		        .place_no(place.getPlaceNo())
		        .place_name(place.getPlaceName())
		        .emp_id(employee.getEmpId())
		        .emp_name(employee.getEmpName())
		        .start_date(pis.getStartDate())
		        .start_time(pis.getStartTime())
		        .end_date(pis.getEndDate())
		        .end_time(pis.getEndTime())
		        .item_no(itemNo)
		        .item_name(itemName)
		        .item_quantity(itemQuantity)
		        .place_schedule_content(pis.getPlaceScheduleContent())
		        .management_no(pis.getManagementNo())
		        .reg_date(pis.getRegDate())
		        .build();

		    // 날짜와 시간 포맷팅
		    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		    // 등록 날짜 포맷팅
		    if (pisv.getReg_date() != null) {
		        pisv.setFormattedRegDate(pisv.getReg_date().format(dateFormatter));
		    }

		    // 이용 시간 포맷팅
		    if (pisv.getStart_date() != null && pisv.getStart_time() != null
		            && pisv.getEnd_date() != null && pisv.getEnd_time() != null) {
		        String formattedUsageTime = pisv.getStart_date().format(dateFormatter) + " "
		                + pisv.getStart_time().format(timeFormatter) + " ~ "
		                + pisv.getEnd_date().format(dateFormatter) + " "
		                + pisv.getEnd_time().format(timeFormatter);
		        pisv.setFormattedUsageTime(formattedUsageTime);
		    }

		    return pisv;
		}


	
	// 일정 겹침 여부 확인하는 메소드
	  public boolean isScheduleOverlapping(Long placeNo, List<Long> itemNoList, String newStartDate, String newStartTime, String newEndDate, String newEndTime) {
		    // 날짜 및 시간 포맷터
		    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		    // 새로 등록할 일정의 시작 및 종료 시간
		    LocalDateTime newStartDateTime = LocalDateTime.parse(newStartDate + " " + newStartTime, dateTimeFormatter);
		    LocalDateTime newEndDateTime = LocalDateTime.parse(newEndDate + " " + newEndTime, dateTimeFormatter);

		    // 기존 일정 조회
		    List<PlaceItemScheduleVo> existingSchedules = placeScheduleVoMapper.getTotalScheduleList();

		    for (PlaceItemScheduleVo schedule : existingSchedules) {
		        // 예외 처리: place_no = 0이고 item_no가 null인 경우, 일정이 겹쳐도 등록 가능
		        if (placeNo == 0 && (itemNoList == null || itemNoList.isEmpty())) {
		            continue; // 이 경우 겹침 검사를 하지 않고 다음 일정으로 넘어갑니다.
		        }

		        // 1. 같은 장소일 경우에만 검사 진행
		        if (schedule.getPlace_no().equals(placeNo)) {
		            // 2. 같은 장소에 같은 기자재를 사용할 경우 검사 진행
		            boolean hasSameItem = false;

		            // itemNoList가 비어있지 않으면 기자재 검사 진행
		            if (itemNoList != null && !itemNoList.isEmpty()) {
		                for (Long itemNo : itemNoList) {
		                    if (schedule.getItem_no() != null && schedule.getItem_no().equals(itemNo)) {
		                        hasSameItem = true;
		                        break;
		                    }
		                }
		            }

		            // 1단계: 장소만 겹침 여부 확인
		            if (!hasSameItem) {
		                // 같은 장소일 경우 시간 겹침 여부 검사
		                LocalDateTime existingStartDateTime = LocalDateTime.parse(schedule.getStart_date() + " " + schedule.getStart_time(), dateTimeFormatter);
		                LocalDateTime existingEndDateTime = LocalDateTime.parse(schedule.getEnd_date() + " " + schedule.getEnd_time(), dateTimeFormatter);

		                // 시간대 겹침 검사
		                if (newStartDateTime.isBefore(existingEndDateTime) && newEndDateTime.isAfter(existingStartDateTime)) {
		                    // 같은 장소에서 시간대가 겹침
		                    return true;
		                }
		            }

		            // 2단계: 장소와 기자재 모두 겹침 여부 확인
		            if (hasSameItem) {
		                // 같은 장소와 같은 기자재가 사용되는 경우 시간 겹침 여부 검사
		                LocalDateTime existingStartDateTime = LocalDateTime.parse(schedule.getStart_date() + " " + schedule.getStart_time(), dateTimeFormatter);
		                LocalDateTime existingEndDateTime = LocalDateTime.parse(schedule.getEnd_date() + " " + schedule.getEnd_time(), dateTimeFormatter);

		                // 시간대 겹침 검사
		                if (newStartDateTime.isBefore(existingEndDateTime) && newEndDateTime.isAfter(existingStartDateTime)) {
		                    // 같은 장소에서 같은 기자재를 같은 시간대에 사용하려고 할 때
		                    return true;
		                }
		            }
		        }
		    }
		    return false; // 모든 조건을 충족하지 않으면 겹침이 없는 것으로 간주
		}
	
	
	
	

	
	// 일정 등록 메소드
	  public PlaceItemSchedule createPlaceSchedule(PlaceItemScheduleVo vo) {
		    try {
		        // 겹침 검사
		        if (isScheduleOverlapping(vo.getPlace_no(), vo.getItemNoList(), vo.getStart_date().toString(), vo.getStart_time().toString(), vo.getEnd_date().toString(), vo.getEnd_time().toString())) {
		            // 겹침이 발생한 경우 null 반환
		            return null;
		        }
		        
		        // Place 조회 및 유효성 검사
		        Place place = placeRepository.findByplaceNo(vo.getPlace_no());
		        if (place == null && vo.getPlace_no() != 0) {
		            throw new IllegalArgumentException("해당 장소를 찾을 수 없습니다: " + vo.getPlace_no());
		        }

		        // Employee 조회 및 유효성 검사
		        Employee employee = employeeRepository.findByempId(vo.getEmp_id());
		        if (employee == null) {
		            throw new IllegalArgumentException("해당 신청인을 찾을 수 없습니다: " + vo.getEmp_id());
		        }

		        // TeacherHistory 조회 및 유효성 검사
		        TeacherHistory teacherHistory = teacherHistoryRepository.findByteacherNo(vo.getTeacher_no());
		        if (teacherHistory == null) {
		            throw new IllegalArgumentException("해당 교사를 찾을 수 없습니다: " + vo.getTeacher_no());
		        }

		        // 아이템 목록 처리 및 관리번호 생성
		        StringBuilder serialNoBuilder = new StringBuilder();
		        Item item = null;

		        if (vo.getPlace_no() == 0) {
		            serialNoBuilder.append("전체");
		        } else {
		            if (vo.getItemNoList() != null && !vo.getItemNoList().isEmpty()) {
		                for (Long itemNo : vo.getItemNoList()) {
		                    item = itemRepository.findByitemNo(itemNo);
		                    if (item == null) {
		                        throw new IllegalArgumentException("해당 기자재를 찾을 수 없습니다: " + itemNo);
		                    }
		                    if (serialNoBuilder.length() > 0) {
		                        serialNoBuilder.append(",");
		                    }
		                    serialNoBuilder.append(item.getItemSerialNo());
		                }
		            } else {
		                throw new IllegalArgumentException("place_no가 0이 아닐 경우에는 적어도 하나의 item_no를 선택해야 합니다.");
		            }
		        }

		        String managementNo = vo.getPlace_no() + "-" + serialNoBuilder.toString();

		        // 일정 생성 및 저장
		        PlaceItemSchedule placeItemSchedule = PlaceItemSchedule.builder()
		                .employee(employee)
		                .place(place)
		                .item(item)
		                .teacherHistory(teacherHistory)
		                .placeScheduleTitle(vo.getPlace_schedule_title())
		                .placeScheduleContent(vo.getPlace_schedule_content())
		                .startDate(vo.getStart_date())
		                .endDate(vo.getEnd_date())
		                .startTime(vo.getStart_time())
		                .endTime(vo.getEnd_time())
		                .managementNo(managementNo)
		                .regDate(vo.getReg_date())
		                .modDate(vo.getMod_date())
		                .build();

		        return placeScheduleRepository.save(placeItemSchedule);

		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
		}

		


    
    
	    




	
    
    

	public List<PlaceItemScheduleVo> selectTotalScheduleList() {
	    // Mapper를 통해 데이터베이스에서 모든 일정을 조회 (VO로 바로 반환)
	    List<PlaceItemScheduleVo> totalSchedule = placeScheduleVoMapper.getTotalScheduleList();

	    for (PlaceItemScheduleVo schedule : totalSchedule) {
	        // item 정보가 없을 때 기본값 설정
	        if (schedule.getItem_no() == null) {
	            schedule.setItem_name("전체"); // item_no가 없을 때 표시할 기본값 설정
	        }
	    }

	    return totalSchedule;
	}
}
