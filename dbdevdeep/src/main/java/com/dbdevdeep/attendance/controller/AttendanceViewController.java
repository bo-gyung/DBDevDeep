package com.dbdevdeep.attendance.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.service.AttendanceService;
import com.dbdevdeep.employee.domain.EmployeeDto;

@Controller
public class AttendanceViewController {

	private final AttendanceService attendanceService;
	
	@Autowired
	public AttendanceViewController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
	
	@GetMapping("/attendance")
	public String selectApproveList(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String empId = authentication.getName();
		
		EmployeeDto result = attendanceService.employeeInfo(empId);
		model.addAttribute("result", result);
		return "attendance/attendance";
	}
}
