 package com.dbdevdeep.employee.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.MySignDto;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.employee.service.TeacherHistoryService;

@Controller
public class EmployeeViewController {
	
	private final EmployeeService employeeService;
	private final TeacherHistoryService teacherHistoryService;
	
	@Autowired
	public EmployeeViewController(EmployeeService employeeService,
			TeacherHistoryService teacherHistoryService) {
		this.employeeService = employeeService;
		this.teacherHistoryService = teacherHistoryService;
	}

	@GetMapping("/login")
	public String loginPage() {
		return "employee/login";
	}
	
	@GetMapping("/signup")
	public String createEmployeePage() {
		return"employee/signup";
	}
	
	@GetMapping("/addressbook")
	public String selectAddressbookList(Model model, EmployeeDto dto) {
		
		List<EmployeeDto> resultList = employeeService.selectYEmployeeList();
		
		model.addAttribute("resultList", resultList);
		
		return "employee/address_book";
	}
	
	@GetMapping("/mypage")
	public String mypagePage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		
		EmployeeDto dto = employeeService.selectEmployeeOne(user.getUsername());
		TeacherHistoryDto thDto = teacherHistoryService.selectHistoryOne(dto);
		
		model.addAttribute("empDto", dto);
		model.addAttribute("thDto", thDto);
		
		return "employee/mypage";
	}
	
	@GetMapping("/mysign")
	public String mysignPage(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		
		List<MySignDto> resultList = employeeService.employeeSignGet(user.getUsername());
		
		model.addAttribute("resultList", resultList);
		
		return "employee/mysign";
	}
	
	@ResponseBody
	@GetMapping("/check-pw/{pwd}")
	public Map<String, String> checkPw(@PathVariable("pwd") String pwd) {
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "비밀번호 확인에 실패하였습니다.");
		
		if(employeeService.checkPw(pwd) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "비밀번호 확인에 성공하였습니다.");			
		} 
		
		return resultMap;
	}

	@GetMapping("/editmypage")
	public String editMyPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		
		EmployeeDto dto = employeeService.selectEmployeeOne(user.getUsername());
		
		model.addAttribute("empDto", dto);
		
		return "employee/edit_my_info";
	}
	
	@GetMapping("/editmypw")
	public String editMyPwPage() {
				
		return "employee/edit_my_pw";
	}
	
	@GetMapping("/allemployee")
	public String allEmployee(Model model) {
		
		List<EmployeeDto> resultList = employeeService.selectEmployeeList();
		
		List<TeacherHistoryDto> historyList = teacherHistoryService.selectClassByOrderLastesList();
		
		model.addAttribute("resultList", resultList);
		model.addAttribute("historyList", historyList);
		
		return "employee/all_employee";
	}
	
	@GetMapping("/employee/{emp_id}")
	public String employeeDetail(Model model, @PathVariable("emp_id") String emp_id) {
		
		EmployeeDto empDto = employeeService.selectEmployeeOne(emp_id);
		
		List<TeacherHistoryDto> thDtoList = teacherHistoryService.selectTeacherHistoryByEmployee(emp_id);
		
		model.addAttribute("thDtoList", thDtoList);
		model.addAttribute("empDto", empDto);
		
		return "employee/employee_detail";
	}

}
