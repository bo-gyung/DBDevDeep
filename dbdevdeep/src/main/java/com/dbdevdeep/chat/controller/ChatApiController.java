 package com.dbdevdeep.chat.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.chat.service.ChatService;
import com.dbdevdeep.chat.vo.ChatMsgVo;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.service.EmployeeService;

@Controller
public class ChatApiController {
	private final ChatService chatService;
	private final EmployeeService employeeService;
	private final FileService fileService;
	
	@Autowired
	public ChatApiController(ChatService chatService, EmployeeService employeeService, 
			FileService fileService) {
		this.chatService = chatService;
		this.employeeService = employeeService;
		this.fileService = fileService;
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
        
        // 두 사람이 한번에 참여한 채팅방이 있는지 조회
        int room_no = chatService.selectPrivateChatRoom(admin_id, emp_id);
        
        if(room_no<1) {
        	
        	// 1. 채팅방 생성
        	room_no = chatService.createChatRoom();
        	
        	// 2. 채팅 참여자 정보 생성
        	Employee admin = employeeService.findEmployeeById(admin_id);
        	String admin_name = admin.getEmpName();
        	
        	Employee emp = employeeService.findEmployeeById(emp_id);
        	String emp_name = emp.getEmpName();
        	
        	int myResult = chatService.createChatMemberInfo(admin_id, emp_name, room_no, 1);
        	int yourResult = chatService.createChatMemberInfo(emp_id, admin_name, room_no, 0);
        	
			if(myResult<0 || yourResult<0) {
				resultMap.put("res_msg", "채팅 참여자 정보 생성 중 오류가 발생하였습니다.");
				return resultMap;
			}
			// 채팅 참여자 상태이력 생성 chat_member_status_history
        	// 방장 (member_status : 1 (입장))
			int adminResult = chatService.createChatMemberStatusHistory(room_no, admin_id, 1, admin_id);
			// 참여자 (member_status : 3 (초대))
			int memberResult = chatService.createChatMemberStatusHistory(room_no, emp_id, 3, admin_id);
			if(adminResult < 0 || memberResult < 0) {
				resultMap.put("res_msg", "채팅 참여자 상태이력 생성 중 오류가 발생하였습니다.");
				return resultMap;
			}
        }
        
        resultMap.put("res_code", "200");
		resultMap.put("res_msg", "채팅방 생성에 성공하였습니다.");
		resultMap.put("room_no", String.valueOf(room_no));
        
        return resultMap;
	}
	
	// 그룹 채팅방 생성
	@ResponseBody
	@PostMapping("/chatroom/group")
	public Map<String,String> createGroupChat(@RequestBody Map<String, List<String>> empIdList) {
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "채팅방 생성 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		String admin_id = user.getUsername();
        List<String> emp_id_list = empIdList.get("emp_id_list");
		
        
        // 기존에 생성된 그룹채팅방이 있는지 조회
        int room_no = chatService.selectGroupChatRoom(admin_id, emp_id_list);
        
        if(room_no<1) {
        	// 없다면 
        	// 1. 채팅방 생성
        	room_no = chatService.createChatRoom();
        	
        	// 방이름 설정
        	Employee admin = employeeService.findEmployeeById(admin_id);
        	String admin_name = admin.getEmpName();
        	String room_name = admin_name+"님이 생성한 그룹 채팅방";
        	
        	// 2-1. 채팅 참여자 정보 생성(방장)
        	int admin_info_result = chatService.createChatMemberInfo(admin_id, room_name, room_no, 1);
        	// 2-2채팅 참여자 상태이력 생성(방장)
			int admin_stat_result = chatService.createChatMemberStatusHistory(room_no, admin_id, 1, admin_id);
			if(admin_info_result < 0 || admin_stat_result < 0) {
				resultMap.put("res_msg", "채팅 참여자 상태이력 생성 중 오류가 발생하였습니다.");
				return resultMap;
			}
			
        	for(String emp_id : emp_id_list) {
        		// 3-1 채팅 참여자 정보 생성(참여자)
        		int emp_info_result = chatService.createChatMemberInfo(emp_id, room_name, room_no, 0);
        		// 2-2채팅 참여자 상태이력 생성(참여자)
        		int emp_stat_result = chatService.createChatMemberStatusHistory(room_no, emp_id, 3, admin_id);
        		if(emp_info_result < 0 || emp_stat_result < 0) {
    				resultMap.put("res_msg", "채팅 참여자 상태이력 생성 중 오류가 발생하였습니다.");
    				return resultMap;
    			}
        	}
        }
        
        resultMap.put("res_code", "200");
		resultMap.put("res_msg", "채팅방 생성에 성공하였습니다.");
		resultMap.put("room_no", String.valueOf(room_no));
        
        return resultMap;
	}
	
	// 채팅 메세지 작성
	@ResponseBody
	@PostMapping("/chatmsg/{roomNo}")
	public Map<String,String> createChatMsg(@PathVariable("roomNo") int room_no, @RequestBody ChatMsgVo vo){

		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "채팅메세지 등록 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String writer_id = user.getUsername();
		vo.setWriter_id(writer_id);
		
		int result = chatService.createChatMsg(vo);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "채팅메세지 등록에 성공하였습니다.");
			// 웹소켓 호출하기
			// 메세지가 새로 등록된 채팅방번호에 속한 참여자가 
		}
		
		return resultMap;
	}
	
	// 채팅 이미지 첨부
	@ResponseBody
	@PostMapping("/chatpic/{roomNo}")
	public Map<String,String> createChatImg(@PathVariable("roomNo") int room_no, 
											@RequestParam("pic") MultipartFile file){

		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "사진 등록 중 오류가 발생하였습니다.");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String writer_id = user.getUsername();
		
		int result = -1;
		
		String savedFileName = fileService.chatPicUpload(file);
		
		ChatMsgVo vo = new ChatMsgVo();
		vo.setRoom_no(room_no);
		vo.setWriter_id(writer_id);
		vo.setChat_content("사진");
		vo.setOri_pic_name(file.getOriginalFilename());
		vo.setNew_pic_name(savedFileName);
		
		result = chatService.createChatPic(vo);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "사진 등록에 성공하였습니다.");
		}
		
		return resultMap;
	}
	
}
