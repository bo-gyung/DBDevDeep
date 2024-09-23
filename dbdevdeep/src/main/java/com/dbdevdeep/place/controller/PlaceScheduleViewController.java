package com.dbdevdeep.place.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.place.domain.PlaceItemScheduleDto;
import com.dbdevdeep.place.service.ItemService;
import com.dbdevdeep.place.service.PlaceScheduleService;
import com.dbdevdeep.place.service.PlaceService;

@Controller
public class PlaceScheduleViewController {

	private final PlaceScheduleService placeScheduleService;
	private final PlaceService placeService;
	private final ItemService itemService;
	private final TeacherHistoryService teacherHistoryService;
	private final EmployeeService employeeService;
	
	@Autowired
	public PlaceScheduleViewController(PlaceService placeService, ItemService itemService,
			TeacherHistoryService teacherHistoryService, EmployeeService employeeService,
			PlaceScheduleService placeScheduleService) {
		this.placeScheduleService = placeScheduleService;
		this.placeService = placeService;
		this.itemService = itemService;
		this.teacherHistoryService = teacherHistoryService;
		this.employeeService = employeeService;
	}
	
	// 화면딴
	@GetMapping("/place_schedule")
	public String selectScheduleList(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        
        model.addAttribute("user", user);
        return "place/place_schedule";
	}
	
	
	// 캘린더 전체조회(Json)
	@GetMapping("totalSchedule")
	@ResponseBody  // JSON 응답을 위해 추가
	public List<PlaceItemScheduleDto> totalScheduleData(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        
       
        
        List<PlaceItemScheduleDto> totalSchedule = placeScheduleService.selectTotalScheduleList();
        
        
        
        return totalSchedule;
        
	}
	
}
