package com.dbdevdeep.chat.controller;


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

import com.dbdevdeep.chat.dto.CustomChatContainerDto;
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
	
	// 채팅 페이지 최초 진입시
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
	
	// 채팅방 목록만 비동기적으로 가져오는 fragment 엔드포인트
	@GetMapping("/chatroom/fragment")
	public String getChatRoomListFragment(Model model) {
	    // 1. 로그인한 사용자의 정보 불러오기 (여기서는 로그인한 사용자 ID만 사용)
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String login_id = user.getUsername();

	    // 2. 사용자가 참여중인 채팅방 목록을 조회
	    List<CustomChatRoomDto> ccrDtoList = chatService.selectChatRoomList(login_id);
	    model.addAttribute("ccrDtoList", ccrDtoList);

	    // fragment만 반환
	    return "chat/chatpage :: chatRoomList";
	}
	
	// 채팅방 입장(채팅방에 속한 메세지 리스트 출력)
	@GetMapping("/chatrooms/{roomNo}")
	public String selectChatListFragment(Model model, @PathVariable("roomNo") int roomNo) {
	    
		// 로그인한 사용자의 정보 불러오기 (여기서는 로그인한 사용자 ID만 사용)
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String login_id = user.getUsername();
	    
	    
		// 방정보 가져오기
	    // 1. 방이름
	    String roomName = chatService.selectChatRoomName(roomNo, login_id);
	    model.addAttribute("roomName", roomName);
	    // 2. 방번호
	    model.addAttribute("roomNo", roomNo);
	    // 3. 방의 정원 (전체 인원)
	    int headCount = chatService.headCountByRoomNo(roomNo);
	    model.addAttribute("headCount", headCount);
	    
	    
		// 메세지 + 읽음확인 + 상태이력 리스트 가져오기
	    List<CustomChatContainerDto> combinedList = chatService.selectmsgHistoryList(roomNo, login_id);
	    model.addAttribute("combinedList", combinedList);
	    
	    
		
		// fragment만 반환
	    return "chat/chatpage :: chatContainer";
	    
	    
	    
	}
	
	// 메인페이지 헤더에 표시될 채팅 읽음 확인 개수 조회
	@ResponseBody
	@GetMapping("/chat/alert/header")
	public Map<String,String> selectChatReadCkeck(){
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "읽지 않은 메세지 조회 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String emp_id = user.getUsername();
		
		int result = chatService.selectChatReadCheckByEmpId(emp_id);
		
		resultMap.put("res_code", "200");
		resultMap.put("res_msg", "읽지 않은 메세지 조회 생성에 성공하였습니다.");
		resultMap.put("unread_count", String.valueOf(result));
		
		return resultMap;
	}
	
}
