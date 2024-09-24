package com.dbdevdeep.chat.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ChatMessageHandler {
	
//	private final WebSocketHandler webSocketHandler;
//	@Autowired
//	public ChatMessageHandler(WebSocketHandler webSocketHandler) {
//		this.webSocketHandler = webSocketHandler;
//	}

    public void handleChatMessage(WebSocketSession session, Map<String, Object> messageMap) {
        String subType = (String) messageMap.get("subType");

        switch (subType) {
            case "PRIVATE":
            	handlePrivateChat(session, messageMap);
                break;
            case "GROUP":
                handleGroupChat(session, messageMap);
                break;
            default:
                // 기본 처리 로직
                break;
        }
    }

    private void handlePrivateChat(WebSocketSession session, Map<String, Object> messageMap) {
        String action = (String) messageMap.get("action");
        switch (action) {
            case "CREATE_ROOM":
                createPrivateChatRoom(session, messageMap);
                break;
            case "SEND_MESSAGE":
                sendPrivateChatMessage(session, messageMap);
                break;
            default:
                break;
        }
    }

    private void handleGroupChat(WebSocketSession session, Map<String, Object> messageMap) {
        String action = (String) messageMap.get("action");
        switch (action) {
            case "CREATE_ROOM":
                createGroupChatRoom(session, messageMap);
                break;
            case "SEND_MESSAGE":
                sendGroupChatMessage(session, messageMap);
                break;
            default:
                break;
        }
    }

    // 개인 및 그룹 채팅 로직 구현 메서드들
    private void createPrivateChatRoom(WebSocketSession session, Map<String, Object> messageMap) {
        // 사용자가 채팅방을 생성한 대화상대에게 채팅방이 생성되었음을 알림받은 곳!
    	// 대화상대의 정보 꺼내기
    	String emp_id = (String) messageMap.get("member_id");
    	Map<String,String> alertMap = new HashMap<String, String>();
    	alertMap.put("emp_id", emp_id);
    	alertMap.put("msg", "chat");
    	
    	// 헤더의 채팅아이콘에 알림 발생
    	
    	// 대화상대가 채팅방에 접속한 상태라면 채팅방 리스트 갱신
    }

    private void sendPrivateChatMessage(WebSocketSession session, Map<String, Object> messageMap) {
        // 개인 채팅 메시지 전송 로직
    }

    private void createGroupChatRoom(WebSocketSession session, Map<String, Object> messageMap) {
        // 그룹 채팅방 생성 로직
    }

    private void sendGroupChatMessage(WebSocketSession session, Map<String, Object> messageMap) {
        // 그룹 채팅 메시지 전송 로직
    }
}