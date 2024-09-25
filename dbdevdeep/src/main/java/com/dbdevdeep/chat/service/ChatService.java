package com.dbdevdeep.chat.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.chat.dto.ChatMsgDto;
import com.dbdevdeep.chat.dto.CustomChatContainerDto;
import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.mybatis.mapper.ChatMapper;
import com.dbdevdeep.chat.vo.ChatMemberInfoVo;
import com.dbdevdeep.chat.vo.ChatMemberStatusHistoryVo;
import com.dbdevdeep.chat.vo.ChatMsgVo;
import com.dbdevdeep.chat.vo.ChatRoomVo;
import com.dbdevdeep.employee.domain.Employee;
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
	
	// 채팅 페이지 나브 바 진입시
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
	
	// 일대일 채팅방 조회
	public int selectPrivateChatRoom(String admin_id, String emp_id) {
		int result = -1;
		
		result = chatMapper.selectPrivateChatRoom(admin_id, emp_id);
	    
		return result;
	}
	
	// 일대일 채팅방 생성
	public int createPrivateChatRoom(String admin_id, String emp_id) {
		
		String lastChatMessage = "";
		ChatRoomVo crVo = new ChatRoomVo();
		crVo.setLast_chat(lastChatMessage);

		chatMapper.createPrivateChatRoom(crVo);
		
		return crVo.getRoom_no();
	}	
	
	// 채팅방 참여 정보 생성
	public int createChatMemberInfo(String admin_id, String emp_id, int room_no) {
		
		int result = -1;
		
		// 방장(로그인한 사용자)
		ChatMemberInfoVo adminCmiVo = new ChatMemberInfoVo();
		adminCmiVo.setMember_id(admin_id);
		adminCmiVo.setRoom_no(room_no);
		
		Employee emp = employeeRepository.findByempId(emp_id);
		adminCmiVo.setRoom_name(emp.getEmpName());
		
		adminCmiVo.setIs_admin(1);
		int admin = chatMapper.createChatMemberInfo(adminCmiVo);
		
		// 참여자(로그인한 사용자가 선택한 사용자)
		ChatMemberInfoVo memberCmiVo = new ChatMemberInfoVo();
		memberCmiVo.setMember_id(emp_id);
		memberCmiVo.setRoom_no(room_no);
		
		Employee ad = employeeRepository.findByempId(admin_id);
		memberCmiVo.setRoom_name(ad.getEmpName());
		
		memberCmiVo.setIs_admin(0);
		int member = chatMapper.createChatMemberInfo(memberCmiVo);
		
		if(admin>0 && member>0) {
			result = 1;
		}
		
		return result;
	}
	
	// 채팅 참여자 상태이력 생성
	public int createChatMemberStatusHistory(int room_no, String member_id, int member_status, String changed_by_id) {

		int result = -1;
		
		ChatMemberStatusHistoryVo cmshVo = new ChatMemberStatusHistoryVo();
		cmshVo.setRoom_no(room_no);
		cmshVo.setMember_id(member_id);
		cmshVo.setMember_status(member_status);
		cmshVo.setChanged_by_id(changed_by_id);
		
		result = chatMapper.createChatMemberStatusHistory(cmshVo);
		
		return result;
	}
	
	// 채팅방 이름 조회
	public String selectChatRoomName(int roomNo, String login_id) {
		ChatMemberInfoVo cmiVo = new ChatMemberInfoVo();
		cmiVo.setMember_id(login_id);
		cmiVo.setRoom_no(roomNo);
		
		return chatMapper.selectChatRoomName(cmiVo);
	}
	
	// 메세지+상태이력 리스트 조회
	public List<CustomChatContainerDto> selectmsgHistoryList(int roomNo, String login_id){
		// 메세지 리스트 조회
		List<ChatMsgVo> msgList = chatMapper.selectChatMsgList(roomNo);
		// 상태이력 리스트 조회
		List<ChatMemberStatusHistoryVo> historyList = chatMapper.selectHistoryList(roomNo);
		
		// 통합 리스트 생성
	    List<CustomChatContainerDto> combinedList = new ArrayList<>();
	    // 메세지 리스트를 CustomChatContainerDto로 변환하여 추가	
	    for (ChatMsgVo msg : msgList) {
	        
	    	String messageType = msg.getWriter_id().equals(login_id) ? "me" : "notMe";
	        Employee e = employeeRepository.findByempId(msg.getWriter_id());
	        
	        CustomChatContainerDto dto = new CustomChatContainerDto(
	            messageType,  // "me" 또는 "notMe"
	            msg.getWriter_id(),
	            e.getEmpName(),
	            messageType.equals("notMe") ? e.getNewPicName() : "",
	            msg.getChat_content(),  // 메시지 내용
	            msg.getReg_time()  // 타임스탬프
	        );
	        combinedList.add(dto);
	    }
	    // 상태이력 리스트를 CustomChatContainerDto로 변환하여 추가
	    for (ChatMemberStatusHistoryVo history : historyList) {
	    	
	    	Employee e = employeeRepository.findByempId(history.getMember_id());
	    	
	        CustomChatContainerDto dto = new CustomChatContainerDto(
	            "history",  // 상태 이력 타입
	            history.getMember_id(),
	            e.getEmpName(),
	            "",
	            String.valueOf(history.getMember_status()),  // 상태 이력 내용 (입장, 퇴장, 초대 등)
	            history.getChange_time()  // 타임스탬프
	        );
	        combinedList.add(dto);
	    }
	    
	    // 타임스탬프 기준으로 리스트 정렬
	    combinedList.sort(Comparator.comparing(CustomChatContainerDto::getTimestamp));

	    return combinedList;
		
	}
	

	// 채팅 메세지 생성
	public int createChatMsg(ChatMsgVo vo) {
		int result = -1; 
		
		result = chatMapper.createChatMsg(vo);
		
		
		
		return result;
	}
	
}
