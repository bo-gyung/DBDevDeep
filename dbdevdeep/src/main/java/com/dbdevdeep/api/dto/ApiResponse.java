package com.dbdevdeep.api.dto;

import java.util.List;

import com.dbdevdeep.document.domain.FolderDto;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {

	private List<EmployeeDto> employees;
	private List<TeacherHistoryDto> teacherHistory;
}
