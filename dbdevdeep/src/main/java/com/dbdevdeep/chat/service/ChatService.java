package com.dbdevdeep.chat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.mybatis.mapper.ChatMapper;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Service
public class ChatService {
	
	private final ChatMapper chatMapper;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public ChatService(ChatMapper chatMapper, EmployeeRepository employeeRepository) {
		this.chatMapper = chatMapper;
		this.employeeRepository = employeeRepository;
	}
	
	public List<CustomChatRoomDto> selectChatRoomList(String emp_id){
		
		// 1. 사용자가 참여중인 채팅방 목록을 조회
		List<CustomChatRoomDto> ccrDtoList = chatMapper.findAllByfromIdAndtoId(emp_id);
		
        // 2. 각 채팅방의 다른 참여자 수를 조회 1명인 경우 상대방의 new_pic_name을 가져온다.
        for (CustomChatRoomDto chatRoom : ccrDtoList) {
        	Map<String, Object> params = new HashMap<>();
            params.put("room_no", chatRoom.getRoom_no());
            params.put("emp_id", emp_id);
            
            String roomPic = chatMapper.otherMemberPic(params);

            if (roomPic != null) {
                // roomPic값이 있을 경우 room_pic에 설정
                chatRoom.setRoom_pic(roomPic);
            } else {
                // null일 경우 학교 로고로 room_pic 설정
                chatRoom.setRoom_pic("8b821ba7a513411f8cacf78926ff4d64.png");
            }
        }
		
        
		return ccrDtoList;
		
        
	}
}
