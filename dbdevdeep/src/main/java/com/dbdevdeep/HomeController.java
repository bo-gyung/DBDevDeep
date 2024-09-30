package com.dbdevdeep;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.service.AttendanceService;
import com.dbdevdeep.document.domain.FileDto;
import com.dbdevdeep.schedule.domain.ScheduleDto;
import com.dbdevdeep.schedule.service.ScheduleService;

@Controller
public class HomeController {
	
	private final AttendanceService attendanceService;
	private final ScheduleService scheduleService;
	private final FileService fileService;
	
	@Autowired
	public HomeController(AttendanceService attendanceService, ScheduleService scheduleService, FileService fileService) {
		this.attendanceService = attendanceService;
		this.scheduleService = scheduleService;
		this.fileService = fileService;
	}
	
	@GetMapping({"", "/"})
	public String home(Model model) {
		
		AttendanceDto dto = attendanceService.findByTodayCheckTime();
		
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        String empId = user.getUsername();
        
        List<ScheduleDto> todaySchedule = scheduleService.selectTodaySchedule(empId);
        
        model.addAttribute("todaySchedule", todaySchedule);
        model.addAttribute("today", LocalDate.now());
        
        // 공용 파일 최신순 가져오기
        List<FileDto> publicFiles = fileService.findPublicFilesOrderByModTimeDesc();
        model.addAttribute("publicFiles", publicFiles);
		
		if(dto != null) {
			model.addAttribute("checktime", dto);			
		}
		
		return "home";
	}
}
