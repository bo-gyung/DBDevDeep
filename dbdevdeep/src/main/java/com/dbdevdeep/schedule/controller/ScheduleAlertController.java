package com.dbdevdeep.schedule.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbdevdeep.schedule.service.ScheduleService;

@RestController
public class ScheduleAlertController {

	private final ScheduleService scheduleService;
	
	public ScheduleAlertController(ScheduleService scheduleService){
		this.scheduleService = scheduleService;
	}
	
    @GetMapping("/scheduleAlert")
    public List<Map<String, Object>> getScheduleAlerts() {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        User user = (User) authentication.getPrincipal();
        String empId = user.getUsername(); // empId 가져오기
    	
        // 서비스에서 준비된 알림 데이터를 반환
        return scheduleService.getAlerts(empId);
    }
}
