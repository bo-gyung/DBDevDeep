package com.dbdevdeep.websocket.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
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
				// roomNo를 Integer로 변환
		        int roomNo = Integer.parseInt(messageMap.get("roomNo").toString());
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

	// 채팅방 메세지 전송 메서드(일대일/단체)
	public void sendPrivateChatMsg(List<String> members, int room_no) {
		
		for (String m : members) {
	        UserSessionInfo userSessionInfo = clients.get(m);
	        
	        if (userSessionInfo != null && userSessionInfo.getSession().isOpen()) {
	        	
	        	try {
	        		
	        		Map<String,String> resultMap = new HashMap<String,String>();
	    			resultMap.put("res_code", "404");
	    			resultMap.put("res_msg", "채팅 전송중 오류가 발생하였습니다.");
	    			
		        	if(userSessionInfo.getNowPage().equals("/chat")) {
		        		// (1) 채팅 페이지에 접속한 사용자
		        		resultMap.put("res_code", "200");
		        		resultMap.put("res_type", "chat");
		        		resultMap.put("now_page", "chat");
		        		resultMap.put("room_in", "N");
		        		
		        		if(userSessionInfo.getNowRoomNo() == room_no) {
		        			// (2) 채팅 페이지에 접속하고, 현재 채팅방에 접속해있는 사용자
		        			resultMap.put("room_in", "Y");
		        			resultMap.put("room_no", String.valueOf(room_no));
		        		}
		        	} else {
		        		// (3) 채팅 페이지에 접속하고 있지 않은 사용자
	        			resultMap.put("res_code", "200");
	        			resultMap.put("res_type", "chat");
	        			resultMap.put("now_page", "no_chat");
		        	}
		        	
		        	userSessionInfo.getSession().sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(resultMap)));
	        	
                } catch (IOException e) {
                    // IOException 발생 시 처리 (예: 메시지 전송 실패)
                    System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    // 기타 예외 처리
                    System.err.println("알 수 없는 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
                
            }
        }

	}


	// 특정 사용자에게 알림을 보내는 메서드
	public void sendAlert(Alert alert) throws IOException {
		UserSessionInfo userSessionInfo = clients.get(alert.getEmployee().getEmpId());
        
        if (userSessionInfo != null && userSessionInfo.getSession().isOpen()) {
        	WebSocketSession session = userSessionInfo.getSession();
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
}