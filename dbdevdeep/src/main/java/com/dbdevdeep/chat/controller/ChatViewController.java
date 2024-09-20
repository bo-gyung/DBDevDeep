package com.dbdevdeep.chat.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.service.ChatService;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.service.EmployeeService;

@Controller
public class ChatViewController {
	
	// 의존성 주입
	private final EmployeeService employeeService;
	private final ChatService chatService;
	@Autowired
	public ChatViewController(EmployeeService employeeService, ChatService chatService) {
		this.employeeService = employeeService;
		this.chatService = chatService;
	}
	
	// 채팅 페이지 이동
	@GetMapping("/chat")
	public String selectChatLsit(Model model) {
		
		// 1. 로그인한 사용자의 정보 불러오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal(); // security 입장의 name(id), pw
		String login_id = user.getUsername();
		EmployeeDto loginEmp = employeeService.selectEmployeeOne(login_id);
		model.addAttribute("loginEmp", loginEmp);
		
		// 2. 사용자가 참여중인 채팅방 목록을 조회
		List<CustomChatRoomDto> ccrDtoList = chatService.selectChatRoomList(login_id);
		model.addAttribute("ccrDtoList", ccrDtoList);
		
		return "chat/chatpage";
	}
	
	// 채팅방 대화내용 상세조회
	
	
}
