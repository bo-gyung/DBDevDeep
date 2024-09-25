package com.dbdevdeep.websocket.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dbdevdeep.alert.config.AlertMessageHandler;
import com.dbdevdeep.alert.domain.Alert;
import com.dbdevdeep.chat.config.ChatMessageHandler;
import com.dbdevdeep.chat.vo.ChatMsgVo;
import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

	private final ChatMessageHandler chatMessageHandler;
	private final AlertMessageHandler alertMessageHandler;
	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet(); // 세션 관리(서버에 접속한 모든 웹소켓 세션)
	private Map<String, UserSessionInfo> clients = new HashMap<String, UserSessionInfo>(); // 현재 서버에 로그인한 직원들
	
	// 유저 세션 정보와 페이지 정보를 포함하는 클래스 정의
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class UserSessionInfo {
	    private WebSocketSession session;
	    private String nowPage;
	    private int nowRoomNo;

    }
	

	@Autowired
	public WebSocketHandler(ChatMessageHandler chatMessageHandler, AlertMessageHandler alertMessageHandler) {
		this.chatMessageHandler = chatMessageHandler;
		this.alertMessageHandler = alertMessageHandler;
	}

	// 클라이언트가 연결되었을 때 동작
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String emp_id = session.getAttributes().get("emp_id") + "";
		UserSessionInfo usi = new UserSessionInfo();
		usi.setSession(session);
		usi.setNowPage("/login");
		usi.setNowRoomNo(0);
		
		clients.put(emp_id, usi); // 로그인 시 clients에 저장
		sessions.add(session);
		
		logger.info("WebSocket connection established: " + session.getAttributes().get("emp_id"));
	}

	// 클라이언트가 웹소켓 서버로 메시지를 전송했을 때
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 클라이언트가 보낸 메시지
		String payload = message.getPayload();
		// json 객체 생성
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> messageMap;

		try {
			// json -> SendMessage 형태 변환
			messageMap = objectMapper.readValue(payload, Map.class);

		} catch (IOException e) {
			logger.error("Failed to parse message payload: " + payload, e);
			session.sendMessage(new TextMessage("Invalid message format"));
			return;
		}

		String type = (String) messageMap.get("type");

		if (type == null) {
			logger.error("Message type is null");
			session.sendMessage(new TextMessage("Message type is required"));
			return;
		}
		
		String emp_id = (String)session.getAttributes().get("emp_id");

		switch (type) {
			case "PAGE_INFO":
	            // 로그인한 사용자의 현재 페이지 위치를 저장
	            String pageUrl = (String) messageMap.get("pageUrl");
	            clients.get(emp_id).setNowPage(pageUrl);
				break;	
			case "ROOM_NO":
	            // 로그인한 사용자가 현재 입장한 채팅방 번호를 저장
	            int roomNo = (int) messageMap.get("roomNo");
	            clients.get(emp_id).setNowRoomNo(roomNo);
				break;	
			default:
				logger.warn("Unhandled message type: " + type);
				break;
		}
	}

	// 연결이 끊겼을 때
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session); // 세션 제거
        String empId = (String) session.getAttributes().get("emp_id");
        if (empId != null) {
            clients.remove(empId);
        }
        logger.info("WebSocket connection closed: {}", session.getId());
	}

	// 일대일 채팅방 메세지 전송 메서드
	public void sendPrivateChatMsg(ChatMsgVo msgVo) {
		// 1. 대상 : 룸넘버에 속한 사용자
		// (1) 채팅 페이지에 접속한 사용자 -> 채팅방목록 리로드
		// (2) 채팅 페이지에 접속하고, 현재 채팅방에 접속해있는 사용자 -> 채팅방목록 리로드 + 채팅메세지목록 리로드
	}

	// 특정 그룹에게 메시지를 전송하는 메서드
	public void sendMessageToGroup(String groupId, String message) {
		// 그룹에 속한 사용자들에게 메시지를 보내는 로직을 추가
	}
	
	// 특정 사용자에게 알림을 보내는 메서드
	public void sendAlert(Alert alert) throws IOException {
		WebSocketSession session = clients.get(alert.getEmployee().getEmpId()).getSession();
		if (session != null && session.isOpen()) {
			try {
				String alertJson = alertMessageHandler.sendAlertMessageToUser(alert);
				session.sendMessage(new TextMessage(alertJson));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}