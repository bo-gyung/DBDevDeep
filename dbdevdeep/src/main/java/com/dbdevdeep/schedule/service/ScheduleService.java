package com.dbdevdeep.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.schedule.domain.Category;
import com.dbdevdeep.schedule.domain.Schedule;
import com.dbdevdeep.schedule.domain.ScheduleDto;
import com.dbdevdeep.schedule.repository.CategoryRepository;
import com.dbdevdeep.schedule.repository.ScheduleRepository;

@SpringBootApplication
@EnableScheduling // 스케줄러 활성화
@Service
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final EmployeeRepository employeeRepository;
	private final CategoryRepository categoryRepository;
	
	// 알림 데이터를 저장할 리스트
    private final List<Map<String, Object>> alertDataList = new ArrayList<>();
	
	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, EmployeeRepository employeeRepository,
			CategoryRepository categoryRepository) {
		this.scheduleRepository = scheduleRepository;
		this.employeeRepository = employeeRepository;
		this.categoryRepository = categoryRepository;
	}
	
	public List<ScheduleDto> selectTotalScheduleList(String empId){
		// 공용 일정 가져오기
	    List<Schedule> publicScheduleList = scheduleRepository.findByCalendarType(0);
	    
	    // 개인 일정 가져오기
	    List<Schedule> privateScheduleList = scheduleRepository.findByCalendarTypeAndEmployee_EmpId(1, empId);
	    
	    // 공용 일정과 개인 일정을 합침
	    List<Schedule> totalScheduleList = new ArrayList<>(publicScheduleList);  // 공용 일정을 추가
	    totalScheduleList.addAll(privateScheduleList);  // 개인 일정 추가

	    // DTO 리스트로 변환
	    List<ScheduleDto> scheduleDtoList = new ArrayList<>();
	    for (Schedule s : totalScheduleList) {
	        ScheduleDto dto = new ScheduleDto().toDto(s);
	        scheduleDtoList.add(dto);
	    }

	    return scheduleDtoList;
	}
	
	public List<ScheduleDto> selectPublicScheduleList() {
		List<Schedule> scheduleList = scheduleRepository.findByCalendarType(0);
		
		List<ScheduleDto> scheduleDtoList = new ArrayList<ScheduleDto>();
		for(Schedule s : scheduleList) {
			ScheduleDto dto = new ScheduleDto().toDto(s);	
			scheduleDtoList.add(dto);
		}
		
		return scheduleDtoList;
	}

	public List<ScheduleDto> selectPrivateScheduleList(String empId) {
		List<Schedule> scheduleList = scheduleRepository.findByCalendarTypeAndEmployee_EmpId(1,empId);
		
		List<ScheduleDto> scheduleDtoList = new ArrayList<ScheduleDto>();
		for(Schedule s : scheduleList) {
			ScheduleDto dto = new ScheduleDto().toDto(s);
			scheduleDtoList.add(dto);
		}
		
		return scheduleDtoList;
	}

	public Schedule createSchedule(ScheduleDto dto) {
	    Employee employee = employeeRepository.findByempId(dto.getEmp_id());
	    Category category = categoryRepository.findByCategoryNo(dto.getCategory_no());
		
		Schedule schedule = Schedule.builder()
				.calendarType(dto.getCalendar_type())
				.scheduleTitle(dto.getSchedule_title())
				.scheduleContent(dto.getSchedule_content())
				.startDate(dto.getStart_date())
				.startTime(dto.getStart_time())
				.endDate(dto.getEnd_date())
				.endTime(dto.getEnd_time())
				.isAllDay(dto.getIs_all_day())
				.schedulePlace(dto.getSchedule_place())
				.repeatType(dto.getRepeat_type())
				.repeatStartDate(dto.getRepeat_start_date())
				.repeatEndDate(dto.getRepeat_end_date())
				.regTime(dto.getReg_time())
				.modTime(dto.getMod_time())
				.employee(employee)
				.category(category)
				.alertType(dto.getAlert_type())
				.build();
		
		return scheduleRepository.save(schedule);
	}

	public ScheduleDto selectScheduleOne(Long schedule_no) {
		Schedule schedule = scheduleRepository.findByScheduleNo(schedule_no);
		
		ScheduleDto dto = ScheduleDto.builder()
				.schedule_no(schedule.getScheduleNo())
				.calendar_type(schedule.getCalendarType())
				.schedule_title(schedule.getScheduleTitle())
				.schedule_content(schedule.getScheduleContent())
				.start_date(schedule.getStartDate())
				.start_time(schedule.getStartTime())
				.end_date(schedule.getEndDate())
				.end_time(schedule.getEndTime())
				.is_all_day(schedule.getIsAllDay())
				.schedule_place(schedule.getSchedulePlace())
				.repeat_type(schedule.getRepeatType())
				.repeat_start_date(schedule.getRepeatStartDate())
				.repeat_end_date(schedule.getRepeatEndDate())
				.reg_time(schedule.getRegTime())
				.mod_time(schedule.getModTime())
				.emp_name(schedule.getEmployee().getEmpName())
				.category_color(schedule.getCategory().getCategoryColor())
				.category_name(schedule.getCategory().getCategoryName())
				.category_no(schedule.getCategory().getCategoryNo())
				.alert_type(schedule.getAlertType())
				.build();
		
		return dto;
	}

	public int deleteSchedule(Long schedule_no) {
		int result = 0;
		try {
			scheduleRepository.deleteById(schedule_no);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Schedule updateschedule(ScheduleDto dto) {
		ScheduleDto temp = selectScheduleOne(dto.getSchedule_no());
		
		temp.setCalendar_type(dto.getCalendar_type());
		temp.setCategory_no(dto.getCategory_no());
		temp.setEmp_id(dto.getEmp_id());
		temp.setSchedule_title(dto.getSchedule_title());
		temp.setSchedule_content(dto.getSchedule_content());
		temp.setStart_date(dto.getStart_date());
		temp.setStart_time(dto.getStart_time());
		temp.setEnd_date(dto.getEnd_date());
		temp.setEnd_time(dto.getEnd_time());
		temp.setIs_all_day(dto.getIs_all_day());
		temp.setSchedule_place(dto.getSchedule_place());
		temp.setRepeat_type(dto.getRepeat_type());
		temp.setRepeat_start_date(dto.getRepeat_start_date());
		temp.setRepeat_end_date(dto.getRepeat_end_date());
		temp.setMod_time(LocalDateTime.now());
		temp.setAlert_type(dto.getAlert_type());
		
		Schedule schedule = temp.toEntity(employeeRepository, categoryRepository);
		Schedule result = scheduleRepository.save(schedule);
		
		return result;
	}
	
	// 매 분마다 실행하여 알림 체크
    @Scheduled(cron = "0 * * * * *") // 매 분의 0초에 실행
    public void checkForAlerts() {
        alertDataList.clear(); // 매번 새롭게 데이터를 저장하기 위해 리스트를 비움

        // alertType이 'OFF'가 아닌 스케줄 데이터를 가져옴
        List<Schedule> schedules = scheduleRepository.findByAlertTypeIsNot("OFF");

        for (Schedule s : schedules) {
            LocalDate startDate = s.getStartDate();
            LocalTime startTime = s.getStartTime();
            LocalDateTime startDateTime = startDate.atTime(startTime);
            String alertType = s.getAlertType();

            LocalDateTime alertTime = null;
            String msg = "";

            if ("ON".equals(alertType)) {
                alertTime = startDateTime;
                msg = "있습니다.";
            } else if ("10".equals(alertType)) {
                alertTime = startDateTime.minusMinutes(10);
                msg = "10분 후에 시작됩니다.";
            } else if ("30".equals(alertType)) {
                alertTime = startDateTime.minusMinutes(30);
                msg = "30분 후에 시작됩니다.";
            } else if ("60".equals(alertType)) {
                alertTime = startDateTime.minusMinutes(60);
                msg = "60분 후에 시작됩니다.";
            }

            // 알림 시간이 현재 시간에 도래했는지 확인
            if (alertTime != null && alertTime.isBefore(LocalDateTime.now()) && alertTime.isAfter(LocalDateTime.now().minusMinutes(1))) {
                // Map으로 알림 데이터를 생성하여 리스트에 추가
            	
                Map<String, Object> alertData = new HashMap<>();
                alertData.put("title", "일정");
                alertData.put("message", "\'" + s.getScheduleTitle() + "\' 일정이 " + msg); // 단일 따옴표 이스케이프
                alertData.put("time", startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                alertData.put("emp_id", s.getEmployee().getEmpId());
                alertDataList.add(alertData);
            }
        }
    }
    
    // 클라이언트 요청 시 알림 데이터를 반환하는 메서드
    public List<Map<String, Object>> getAlerts(String empId) {
    	
        return alertDataList.stream()
            .filter(alert -> empId.equals(alert.get("emp_id")))
            .collect(Collectors.toList());
    }

}
