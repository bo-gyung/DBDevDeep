package com.dbdevdeep.chat.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.chat.dto.CustomChatContainerDto;
import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.mybatis.mapper.ChatMapper;
import com.dbdevdeep.chat.vo.ChatMemberInfoVo;
import com.dbdevdeep.chat.vo.ChatMemberStatusHistoryVo;
import com.dbdevdeep.chat.vo.ChatMsgVo;
import com.dbdevdeep.chat.vo.ChatRoomVo;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.websocket.config.WebSocketHandler;

@Service
public class ChatService {
	
	private final ChatMapper chatMapper;
	private final EmployeeRepository employeeRepository;
	private final WebSocketHandler webSocketHandler;
	
	@Autowired
	public ChatService(ChatMapper chatMapper, EmployeeRepository employeeRepository, 
			WebSocketHandler webSocketHandler) {
		this.chatMapper = chatMapper;
		this.employeeRepository = employeeRepository;
		this.webSocketHandler = webSocketHandler;
	}
	
	// 채팅 페이지 진입시
	public List<CustomChatRoomDto> selectChatRoomList(String emp_id){
		
		// 1. 사용자가 참여중인 채팅방 목록을 조회
		List<CustomChatRoomDto> ccrDtoList = chatMapper.findAllByfromIdAndtoId(emp_id);
		
        for (CustomChatRoomDto chatRoom : ccrDtoList) {
        	Map<String, Object> params = new HashMap<>();
            params.put("room_no", chatRoom.getRoom_no());
            params.put("emp_id", emp_id);
            
            // 2. 각 채팅방의 다른 참여자 수를 조회 1명인 경우 상대방의 new_pic_name을 가져온다.
            List<String> members = chatMapper.otherMemberIds(params);
            
            if (members.isEmpty()) {
            	// 채팅방에 사용자 혼자 있을 경우
            	chatRoom.setRoom_pic("8b821ba7a513411f8cacf78926ff4d64.png");
            } else if (members.size() == 1) {
                // 채팅방에 사용자가 1명인 경우(일대일 채팅방)
            	Employee e = employeeRepository.findByempId(members.get(0));
            	chatRoom.setRoom_pic(e.getNewPicName());
            } else {
                // 리스트에 멤버가 2명 이상인 경우(단체채팅방)
            	chatRoom.setRoom_pic("8b821ba7a513411f8cacf78926ff4d64.png");
            }
            
            // 2. 각 채팅방의 읽지않은 메세지 조회
            int unread_count = chatMapper.countChatReadCheckByEmpIdAndRoomNo(params);
            chatRoom.setChat_read_check(unread_count);

        }
		return ccrDtoList;
	}
	
	// 일대일 채팅방 조회
	public int selectPrivateChatRoom(String admin_id, String emp_id) {
		int result = -1;
		
		result = chatMapper.selectPrivateChatRoom(admin_id, emp_id);
	    
		return result;
	}
	
	// 그룹 채팅방 조회
	public int selectGroupChatRoom(String admin_id, List<String> emp_id_list) {
		int result = -1;
		
		int empCount = emp_id_list.size() + 1; // 관리자 포함한 인원 수
		result = chatMapper.selectGroupChatRoom(admin_id, emp_id_list, empCount);
		
		return result;
	}
	
	// 채팅방 생성
	public int createChatRoom() {
		
		String lastChatMessage = "";
		ChatRoomVo crVo = new ChatRoomVo();
		crVo.setLast_chat(lastChatMessage);

		chatMapper.createPrivateChatRoom(crVo);
		
		return crVo.getRoom_no();
	}	
	
	// 채팅방 참여 정보 생성
	public int createChatMemberInfo(String my_id, String room_name, int room_no,int is_admin) {
		
		int result = -1;
		
		ChatMemberInfoVo memberCmiVo = new ChatMemberInfoVo();
		memberCmiVo.setMember_id(my_id);
		memberCmiVo.setRoom_no(room_no);
		memberCmiVo.setRoom_name(room_name);
		memberCmiVo.setIs_admin(is_admin);
		int member = chatMapper.createChatMemberInfo(memberCmiVo);
		
		if(member>0) {
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
	
	// 메세지+읽음확인+상태이력 리스트 조회
	public List<CustomChatContainerDto> selectmsgHistoryList(int roomNo, String login_id){
		
		// 해당 채팅방에서 읽지않은 메세지 읽음처리하기
		// 1. 사용자가 읽지않은 메세지 조회
		List<ChatMsgVo> unreadMsgList = chatMapper.selectUnreadCheck(roomNo, login_id);
		
		if(unreadMsgList.size()>0) {
			// 2. 읽지않은 메세지가 존재한다면
			// (1) 읽음처리
			for(ChatMsgVo msg : unreadMsgList) {
				chatMapper.createChatReadCheck(msg.getMsg_no(),login_id);
			}
			// (2) 웹소켓 호출
			// 채팅방 참여자 목록 조회
			Map<String, Object> params = new HashMap<>();
			params.put("room_no", roomNo);
			params.put("emp_id", login_id);
			List<String> members = chatMapper.otherMemberIds(params);
			// 웹소켓 핸들러 호출
			webSocketHandler.readChatMsg(members,roomNo);
		}
	    
		// 메세지 리스트 조회
		List<ChatMsgVo> msgList = chatMapper.selectChatMsgList(roomNo, login_id);
		// 상태이력 리스트 조회
		List<ChatMemberStatusHistoryVo> historyList = chatMapper.selectHistoryList(roomNo, login_id);
		
		// 통합 리스트 생성
	    List<CustomChatContainerDto> combinedList = new ArrayList<>();
	    // 메세지 리스트를 CustomChatContainerDto로 변환하여 추가	
	    for (ChatMsgVo msg : msgList) {
	        
	    	String messageType = msg.getWriter_id().equals(login_id) ? "me" : "notMe";
	    	// 메세지 작성자 정보
	        Employee e = employeeRepository.findByempId(msg.getWriter_id());
	        // 메세지의 읽음확인 갯수
	        int readCheck = chatMapper.countChatReadCheckByMsgNo(msg.getMsg_no());
	        
	        CustomChatContainerDto dto = new CustomChatContainerDto(
	            messageType,  // "me" 또는 "notMe"
	            msg.getWriter_id(),
	            e.getEmpName(),
	            messageType.equals("notMe") ? e.getNewPicName() : "",
	            msg.getChat_content(),  // 메시지 내용
	            msg.getOri_pic_name(),
	            msg.getNew_pic_name(),
	            msg.getReg_time(),  // 타임스탬프
	            readCheck
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
	            "",
	            "",
	            history.getChange_time(),  // 타임스탬프
	            0
	        );
	        combinedList.add(dto);
	    }
	    
	    // 타임스탬프 기준으로 리스트 정렬
	    combinedList.sort(Comparator.comparing(CustomChatContainerDto::getTimestamp));
	    
	    return combinedList;
		
	}
	
	// 채팅방에 참여중인 전체 인원수(정원) 구하기
	public int headCountByRoomNo(int room_no) {
		int result = -1;
		result = chatMapper.headCountByRoomNo(room_no);
		return result;
	}
	

	// 채팅 메세지 생성
	public int createChatMsg(ChatMsgVo vo) {
		int result = -1; 
		
		result = chatMapper.createChatMsg(vo);
		if(result > 0) {
			
			ChatMsgVo newVo = chatMapper.selectChatMsgVo(vo.getMsg_no());
			
			// 채팅방 정보 업데이트 (라스트챗, 라스트타임)
			chatMapper.updateChatRoom(newVo);
			// 작성자 읽음확인
			chatMapper.createChatReadCheck(vo.getMsg_no(),vo.getWriter_id());
			
			// 채팅 메세지가 생성된 채팅방의 참여중인 인원 리스트 (메세지 작성자 제외)
			Map<String, Object> params = new HashMap<>();
			params.put("room_no", vo.getRoom_no());
			params.put("emp_id", vo.getWriter_id());
			
			List<String> members = chatMapper.otherMemberIds(params);
			
			if (members.isEmpty()) {
				// 혼자 있는 채팅방일때
			} else if (members.size() > 0) {
				// 메세지 작정자 이외의 참여자가 존재할때
				// 웹소켓 핸들러 호출
				webSocketHandler.sendChatMsg(members,vo.getRoom_no());
			}
		}

		return result;
	}
	
	// 채팅 이미지 생성
	public int createChatPic(ChatMsgVo vo) {
		int result = -1; 
		
		result = chatMapper.createChatPic(vo);
		if(result > 0) {
			
			ChatMsgVo newVo = chatMapper.selectChatMsgVo(vo.getMsg_no());
			
			// 채팅방 정보 업데이트 (라스트챗, 라스트타임)
			chatMapper.updateChatRoom(newVo);
			// 작성자 읽음확인
			chatMapper.createChatReadCheck(vo.getMsg_no(),vo.getWriter_id());
			
			// 채팅 메세지가 생성된 채팅방의 참여중인 인원 리스트 (메세지 작성자 제외)
			Map<String, Object> params = new HashMap<>();
			params.put("room_no", vo.getRoom_no());
			params.put("emp_id", vo.getWriter_id());
			
			List<String> members = chatMapper.otherMemberIds(params);
			
			if (members.isEmpty()) {
				// 혼자 있는 채팅방일때
			} else if (members.size() > 0) {
				// 메세지 작정자 이외의 참여자가 존재할때
				// 웹소켓 핸들러 호출
				webSocketHandler.sendChatMsg(members,vo.getRoom_no());
			}
		}

		return result;
	}
	
	// 메인페이지 헤더에 표시될 채팅 읽음 확인 개수 조회
	public int selectChatReadCheckByEmpId(String emp_id) {
		int result = -1;
		
		result = chatMapper.countChatReadCheckByEmpId(emp_id);
		return result;
	}
	

	
}
