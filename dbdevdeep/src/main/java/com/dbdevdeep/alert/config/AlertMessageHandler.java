package com.dbdevdeep.alert.config;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class AlertMessageHandler {

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
