package com.dbdevdeep.place.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.place.domain.PlaceDto;
import com.dbdevdeep.place.service.PlaceService;

@Controller
public class PlaceViewController {
	
	private final PlaceService placeService;
	private final EmployeeService employeeService;
	
	@Autowired
	public PlaceViewController(PlaceService placeService, EmployeeService employeeService) {
		this.placeService = placeService;
		this.employeeService = employeeService;
	}
	
	// 수정하기
	@GetMapping("/place/update/{place_no}")
	public String updatePlace(Model model, @PathVariable("place_no") Long place_no) {
		
		// 1. 로그인한 사람 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id);
	    
	    model.addAttribute("e_dto", e_dto);
	    
	    PlaceDto dto = placeService.selectPlaceOne(place_no);
	    model.addAttribute("dto", dto);
	    
	    return "place/update";
		
	}
	
	
	// 상세 조회
	@GetMapping("/place/{place_no}")
	public String detailPlace(Model model, @PathVariable("place_no") Long place_no) {
		
		// 1. 로그인한 사람 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id);
	    
	    model.addAttribute("e_dto", e_dto);
		// 게시글 조회
	    PlaceDto dto = placeService.selectPlaceOne(place_no);
	    

	    model.addAttribute("dto",dto);
	    
	    return "place/detail";
	}
	
	// 등록
	@GetMapping("/place/create")
	public String createPlace(Model model) {
		
		// 1. 로그인한 사람 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id); 
	    
	    
	   
	    model.addAttribute("writer", e_dto);
	    
	    
		return "place/create";
	}
	
	
	
	
	// 조회
	@GetMapping("/place")
	public String selectPlaceList(Model model, PlaceDto dto) {
	// 리스트출력	
	List<PlaceDto> resultList = placeService.selectPlaceList(dto);
	model.addAttribute("resultList",resultList);
	return "place/list";
	}
}
