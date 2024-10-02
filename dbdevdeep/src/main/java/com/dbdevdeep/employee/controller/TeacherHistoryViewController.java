package com.dbdevdeep.employee.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.employee.vo.GradeClassGroup;
import com.dbdevdeep.employee.vo.GradeClassRequest;

@Controller
public class TeacherHistoryViewController {

	private final TeacherHistoryService teacherHistoryService;
	private final EmployeeService employeeService;
	
	@Autowired
	public TeacherHistoryViewController(TeacherHistoryService teacherHistoryService,
			EmployeeService employeeService) {
		this.teacherHistoryService = teacherHistoryService;
		this.employeeService = employeeService;
	}
	
	@GetMapping("/grade-class")
	public String GroupByYearList(Model model, GradeClassGroup group) {
		List<GradeClassGroup> resultList = teacherHistoryService.GroupByYearList();
		
		model.addAttribute("resultList", resultList);
		
		return "employee/class_by_year";
	}
	
	@GetMapping("/grade-class/{t_year}")
	public String selectClassByYearList(@PathVariable("t_year") String t_year, Model model) {
		List<TeacherHistoryDto> resultList = teacherHistoryService.selectClassByYearList(t_year);
		List<EmployeeDto> empList = employeeService.selectEmployeeByNotTeacher(t_year);
		
		int maxClass = 0;
		
		Map<String, List<TeacherHistoryDto>> gradeToClassMap = new HashMap<>();

    GradeClassRequest GCR = new GradeClassRequest();
    GCR.setT_year(t_year);
    
    int grade_1 = 0;
    int grade_2 = 0;
    int grade_3 = 0;
    int grade_4 = 0;
    int grade_5 = 0;
    int grade_6 = 0;
		
		// 학년 별 선생님 정보
		for (int grade = 1; grade <= 6; grade++) {
	        gradeToClassMap.put(grade + "", new ArrayList<>());
	    }
		
		for (TeacherHistoryDto dto : resultList) {
	        int grade = dto.getGrade();
	        int gradeClass = dto.getGrade_class();
	        
	        if (gradeClass > maxClass) {
	            maxClass = gradeClass;
	        }
	        
	        switch(dto.getGrade()) {
	        case 1: grade_1++; break;
	        case 2: grade_2++; break;
	        case 3: grade_3++; break;
	        case 4: grade_4++; break;
	        case 5: grade_5++; break;
	        case 6: grade_6++; break;
	        }

	        gradeToClassMap.get(grade + "").add(dto);
	    }
		
		GCR.setGrade_1(grade_1);
		GCR.setGrade_2(grade_2);
		GCR.setGrade_3(grade_3);
		GCR.setGrade_4(grade_4);
		GCR.setGrade_5(grade_5);
		GCR.setGrade_6(grade_6);
		
		model.addAttribute("GCR", GCR);
		model.addAttribute("empList", empList);
		model.addAttribute("resultList", resultList);
		model.addAttribute("maxClass", maxClass);
		model.addAttribute("gradeToClassMap", gradeToClassMap);
		
		return "employee/grade_class_detail";
	}
	
}
