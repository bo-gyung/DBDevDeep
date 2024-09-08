package com.dbdevdeep.attendance.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.service.AttendanceService;

@Controller
public class AttendanceApiController {
	
	private final AttendanceService attendanceService;
	
	@Autowired
	public AttendanceApiController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}

	@ResponseBody
	@PostMapping("/check-in")
	public Map<String, String> employeeCheckIn(@RequestBody String emp_id) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "출근시간 기록 중 오류가 발생하였습니다");
		
		if(attendanceService.employeeCheckIn(emp_id) == 1) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "출근시간 기록에 성공하였습니다");
		}
		
		return resultMap;
	}
	
	@ResponseBody
	@PostMapping("/check-out")
	public Map<String, String> employeeCheckOut( @RequestParam("attend_no") String attendNo,
      @RequestParam("attend_date") String attendDate,
      @RequestParam("check_in_time") String checkInTime,
      @RequestParam("emp_id") String empId) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "출근시간 기록 중 오류가 발생하였습니다");
		
		AttendanceDto dto = new AttendanceDto();
		dto.setAttend_no(Long.parseLong(attendNo));
		dto.setAttend_date(LocalDate.parse(attendDate, DateTimeFormatter.ISO_DATE));
		dto.setCheck_in_time(LocalDateTime.parse(checkInTime, DateTimeFormatter.ISO_DATE_TIME));
		dto.setEmp_id(empId);
		
		if(attendanceService.employeeCheckOut(dto) == 1) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "출근시간 기록에 성공하였습니다");
		}
		
		return resultMap;
	}
}
