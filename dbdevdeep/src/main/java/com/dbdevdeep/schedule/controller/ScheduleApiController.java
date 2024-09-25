package com.dbdevdeep.schedule.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.schedule.domain.ScheduleDto;
import com.dbdevdeep.schedule.service.ScheduleService;

@Controller
public class ScheduleApiController {
	
	private final ScheduleService scheduleService;
	
	@Autowired
	public ScheduleApiController(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}
	
	@ResponseBody
	@PostMapping("/schedule")
	public Map<String,String> createSchedule(@RequestBody ScheduleDto dto){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		dto.setEmp_id(empId);
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "일정 등록 중 오류가 발생하였습니다.");
		
		if(scheduleService.createSchedule(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정이 성공적으로 등록되었습니다.");
		} else {
			resultMap.put("res_msg", "일정 등록 중 예외가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
	@ResponseBody
	@DeleteMapping("/schedule/{schedule_no}")
	public Map<String,String> deleteSchedule(@PathVariable("schedule_no") Long schedule_no){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "일정 삭제 중 오류가 발생하였습니다.");
		
		if(scheduleService.deleteSchedule(schedule_no) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정이 성공적으로 삭제되었습니다.");
		} 
		
		return resultMap;
	}
	
	@ResponseBody
	@PutMapping("/schedule/{schedule_no}")
	public Map<String,String> updateSchedule(@RequestBody ScheduleDto dto){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		dto.setEmp_id(empId);
		
	    // 로컬 날짜와 시간을 조합하여 LocalDateTime으로 변환
	    LocalDate startDate = dto.getStart_date();
	    LocalTime startTime = dto.getStart_time();
	    LocalDate endDate = dto.getEnd_date();
	    LocalTime endTime = dto.getEnd_time();
	    
	    // DTO에 변환된 LocalDateTime 설정
	    dto.setStart_date(startDate); // LocalDate
	    dto.setStart_time(startTime); // LocalTime
	    dto.setEnd_date(endDate); // LocalDate
	    dto.setEnd_time(endTime); // LocalTime
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "일정 수정 중 오류가 발생하였습니다.");
		
		if(scheduleService.updateschedule(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정이 성공적으로 수정되었습니다.");
		} 
		
		return resultMap;
	}
	
}
