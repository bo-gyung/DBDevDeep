package com.dbdevdeep.alert.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.dbdevdeep.alert.domain.Alert;

@Component
public class AlertMessageHandler {
	
	private Map<String, WebSocketSession> clients = new HashMap<>();
	
	public void sendAlertMessageToUser(WebSocketSession session, Map<String, Object> messageMap) {
		
	}
	
	public void sendAlertMessageToUser(String receiver, Alert alert) {
		WebSocketSession session = clients.get(receiver);
//        if (session != null && session.isOpen()) {
//            Map<String, Object> messageMap = new HashMap<>();
//            messageMap.put("type", "alert");
//
//            Map<String, Object> alertData = new HashMap<>();
//            alertData.put("id", alert.getAlertNo());
//            alertData.put("title", alert.getApproTitle());
//            alertData.put("content", alert.getApproContent());
//            alertData.put("timestamp", alert.getAlarmTime().toString());
//            alertData.put("status", alert.isReadYn() ? "read" : "unread");
//
//            messageMap.put("data", Map.of("alert", alertData, "user", Map.of("id", receiverId, "name", "홍길동"))); // 예시 사용자 이름
//
//            sendMessage(session, messageMap);
//        }
	}

//	public void handleAlertMessage(WebSocketSession session, Map<String, Object> messageMap) {
//		String subType = (String) messageMap.get("subType");
//
//		switch (subType) {
//		case "PRIVATE":
//			handlePrivateChat(session, messageMap);
//			break;
//		case "GROUP":
//			handleGroupChat(session, messageMap);
//			break;
//		default:
//			// 기본 처리 로직
//			break;
//		}
//	}
//
//	private void handlePrivateChat(WebSocketSession session, Map<String, Object> messageMap) {
//		String action = (String) messageMap.get("action");
//		switch (action) {
//		case "CREATE_ROOM":
//			createPrivateChatRoom(session, messageMap);
//			break;
//		case "SEND_MESSAGE":
//			sendPrivateChatMessage(session, messageMap);
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void handleGroupChat(WebSocketSession session, Map<String, Object> messageMap) {
//		String action = (String) messageMap.get("action");
//		switch (action) {
//		case "CREATE_ROOM":
//			createGroupChatRoom(session, messageMap);
//			break;
//		case "SEND_MESSAGE":
//			sendGroupChatMessage(session, messageMap);
//			break;
//		default:
//			break;
//		}
//	}
}
