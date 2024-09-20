package com.dbdevdeep.schedule.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.approve.domain.VacationRequestDto;
import com.dbdevdeep.approve.service.ApproveService;
import com.dbdevdeep.schedule.domain.CategoryDto;
import com.dbdevdeep.schedule.domain.HolidayDto;
import com.dbdevdeep.schedule.domain.ScheduleDto;
import com.dbdevdeep.schedule.service.CategoryService;
import com.dbdevdeep.schedule.service.HolidayService;
import com.dbdevdeep.schedule.service.ScheduleService;

@Controller
public class ScheduleViewController {
	
	private final CategoryService categoryService;
	private final HolidayService holidayService;
	private final ScheduleService scheduleService;
	private final ApproveService approveService;
	
	@Autowired
	public ScheduleViewController(CategoryService categoryService, HolidayService holidayService, ScheduleService scheduleService,
			ApproveService approveService) {
		this.categoryService = categoryService;
		this.holidayService = holidayService;
		this.scheduleService = scheduleService;
		this.approveService = approveService;
	}
	
	@GetMapping("/schedule")
	public String selectscheduleList(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		List<CategoryDto> publicCategoryList = categoryService.selectPublicCategoryList();
		List<CategoryDto> privateCategoryList = categoryService.selectPrivateCategoryList(empId);
		model.addAttribute("publicCategoryList", publicCategoryList);
		model.addAttribute("privateCategoryList", privateCategoryList);
		
		return "schedule/schedule";
	}
	
    // 새로운 JSON 반환 메서드
    @GetMapping("/getHolidayData")
    @ResponseBody // JSON 형태로 반환
    public List<HolidayDto> getHolidayData() {
        return holidayService.selectHolidayList(); // 공휴일 데이터를 JSON으로 반환
    }
    
    // 전체 일정 데이터 반환 메서드
    @GetMapping("/getTotalScheduleData")
    @ResponseBody
    public List<ScheduleDto> getTotalScheduleData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        String empId = user.getUsername();
        
        List<ScheduleDto> combinedSchedule = new ArrayList<>();

        // 전체 일정 데이터 가져오기
        List<ScheduleDto> totalSchedules = scheduleService.selectTotalScheduleList(empId);
        combinedSchedule.addAll(totalSchedules); // 기존 전체 일정 추가

        // 휴가 요청 데이터 가져오기
        List<VacationRequestDto> vacationRequest = approveService.selectApprovedVacationRequest(empId);
        for (VacationRequestDto vacation : vacationRequest) {
            ScheduleDto vacationSchedule = new ScheduleDto();
            vacationSchedule.setEmp_id(vacation.getApprove().getEmployee().getEmpId());
            vacationSchedule.setSchedule_title(getVacationTypeTitle(vacation.getVac_type())); // 휴가 유형 제목

            // start_time과 end_time에서 DATE와 TIME으로 쪼개기
            vacationSchedule.setStart_date(vacation.getStart_time().toLocalDate());
            vacationSchedule.setStart_time(vacation.getStart_time().toLocalTime());
            vacationSchedule.setEnd_date(vacation.getEnd_time().toLocalDate());
            vacationSchedule.setEnd_time(vacation.getEnd_time().toLocalTime());
            vacationSchedule.setIs_all_day("Y");
            
            vacationSchedule.setCategory_color("81B3FF");
            vacationSchedule.setCalendar_type(1); // 기본값
            vacationSchedule.setCategory_no((long) 3); // 기본값
            combinedSchedule.add(vacationSchedule); // 휴가 요청 추가
        }
        
        return combinedSchedule;
    }
    
    // 공용 일정 데이터 반환 메서드
    @GetMapping("/getPublicScheduleData")
    @ResponseBody // JSON 형태로 반환
    public List<ScheduleDto> getPublicScheduleData() {
        return scheduleService.selectPublicScheduleList(); // 공용 일정 데이터를 JSON으로 반환
    }

    // 개인 일정 데이터 반환 메서드
    @GetMapping("/getPrivateScheduleData")
    @ResponseBody // JSON 형태로 반환
    public List<ScheduleDto> getPrivateScheduleData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        String empId = user.getUsername();

        List<ScheduleDto> combinedSchedule = new ArrayList<ScheduleDto>();

        // 개인 일정 데이터 가져오기
        List<ScheduleDto> privateSchedule = scheduleService.selectPrivateScheduleList(empId);
        combinedSchedule.addAll(privateSchedule); // 기존 일정 추가

        // 휴가 요청 데이터 가져오기
        List<VacationRequestDto> vacationRequest = approveService.selectApprovedVacationRequest(empId);
        for (VacationRequestDto vacation : vacationRequest) {
            ScheduleDto vacationSchedule = new ScheduleDto();
            vacationSchedule.setEmp_id(vacation.getApprove().getEmployee().getEmpId());
            vacationSchedule.setSchedule_title(getVacationTypeTitle(vacation.getVac_type())); // 휴가 유형 제목

            // start_time과 end_time에서 DATE와 TIME으로 쪼개기
            vacationSchedule.setStart_date(vacation.getStart_time().toLocalDate());
            vacationSchedule.setStart_time(vacation.getStart_time().toLocalTime());
            vacationSchedule.setEnd_date(vacation.getEnd_time().toLocalDate());
            vacationSchedule.setEnd_time(vacation.getEnd_time().toLocalTime());
            vacationSchedule.setIs_all_day("Y");
            
            vacationSchedule.setCategory_color("81B3FF");
            vacationSchedule.setCalendar_type(1); // 기본값
            vacationSchedule.setCategory_no((long) 3); // 기본값
            combinedSchedule.add(vacationSchedule); // 휴가 요청 추가
        }

        return combinedSchedule; // 두 데이터 리스트를 합쳐 반환
    }
    
    // 휴가 유형 제목 반환 메소드
    private String getVacationTypeTitle(int vacType) {
        switch (vacType) {
            case 0: return "연차";
            case 1: return "공가";
            case 2: return "병가";
            case 3: return "조퇴";
            case 4: return "경조사";
            case 5: return "출산휴가";
            case 6: return "오전반차";
            case 7: return "오후반차";
            default: return "알 수 없는 휴가 유형";
        }
    }
    
    @ResponseBody
    @GetMapping("/schedule/{schedule_no}")
    public ScheduleDto selectScheduleOne(@PathVariable("schedule_no") Long schedule_no) {
    	ScheduleDto dto = scheduleService.selectScheduleOne(schedule_no);
    	
    	return dto;
    }
}
