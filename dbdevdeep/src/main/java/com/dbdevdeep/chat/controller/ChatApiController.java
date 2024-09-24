 package com.dbdevdeep.chat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.chat.service.ChatService;
import com.dbdevdeep.chat.vo.ChatMemberInfoVo;
import com.dbdevdeep.employee.domain.EmployeeDto;

@Controller
public class ChatApiController {
	private final ChatService chatService;
	
	@Autowired
	public ChatApiController(ChatService chatService) {
		this.chatService = chatService;
	}
	
	// 일대일 채팅방 생성
	@ResponseBody
	@PostMapping("/chatroom/private")
	public Map<String,String> createChatroom(@RequestBody EmployeeDto dto) {
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "채팅방 생성 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		String admin_id = user.getUsername();
        String emp_id = dto.getEmp_id(); 
        
        // 두 사람이 한번에 참여한 채팅방이 있는 지 조회
        int room_no = chatService.selectPrivateChatRoom(admin_id, emp_id);
        if(room_no<0) {
        	// 없다면 
        	// 1. 채팅방 생성
        	room_no = chatService.createPrivateChatRoom(admin_id, emp_id);
        	// 2. 채팅 참여자 정보 생성
        	int result = chatService.createChatMemberInfo(admin_id, emp_id, room_no);
			if(result<0) {
				resultMap.put("res_msg", "채팅 참여자 정보 생성 중 오류가 발생하였습니다.");
				return resultMap;
			}
			// 채팅 참여자 상태이력 생성 chat_member_status_history
        	// 방장 (member_status : 1 (입장))
			int adminResult = chatService.createChatMemberStatusHistory(room_no, admin_id, 1, admin_id);
			// 참여자 (member_status : 3 (초대))
			int memberResult = chatService.createChatMemberStatusHistory(room_no, emp_id, 3, admin_id);
			if(adminResult<0 || memberResult<0) {
				resultMap.put("res_msg", "채팅 참여자 상태이력 생성 중 오류가 발생하였습니다.");
				return resultMap;
			}
        }
        
        resultMap.put("res_code", "200");
		resultMap.put("res_msg", "채팅방 생성에 성공하였습니다.");
		resultMap.put("room_no", String.valueOf(room_no));
        
        return resultMap;
	}
	
	// 채팅방 입장
	
}
