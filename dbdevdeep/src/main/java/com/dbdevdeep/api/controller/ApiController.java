package com.dbdevdeep.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbdevdeep.api.dto.ApiResponse;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.employee.service.TeacherHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

	private final EmployeeService employeeService;
	private final TeacherHistoryService teacherHistoryService;

	@GetMapping("/emp-tree")
	public ApiResponse getEmployeeTreeItems() {

		List<EmployeeDto> employeeList = employeeService.selectYEmployeeList();

		List<TeacherHistoryDto> teacherDtoList = teacherHistoryService.selectClassByOrderLastesList();

		return new ApiResponse(employeeList, teacherDtoList);

	}
}
