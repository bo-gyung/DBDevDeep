package com.dbdevdeep.chat.config;

import java.io.IOException;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ChatMessageHandler chatMessageHandler;
    private final AlertMessageHandler alertMessageHandler;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet(); // 세션 관리

    @Autowired
    public WebSocketHandler(ChatMessageHandler chatMessageHandler, 
    		AlertMessageHandler alertMessageHandler) {
        this.chatMessageHandler = chatMessageHandler;
        this.alertMessageHandler = alertMessageHandler;
    }

    // 클라이언트가 연결되었을 때 동작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("WebSocket connection established: " + session.getId());
    }

    // 클라이언트가 웹소켓 서버로 메시지를 전송했을 때
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageMap;
        
        // 대분류 -> 중분류 -> 소분류. 총 세단계로 분류
        // (type -> subType -> Action)
        
        // type : CHAT / ALERT [ 채팅(개인/단체/채팅알림) / 알림(공지/문서/일정?) ]
        // subType : PRIVATE & GROUP / 알림분류1 & 알림분류2
        // action : CREATE_ROOM & SEND_MESSAGE / 알림액션1 & 알림액션2

        try {
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

        switch (type) {
            case "CHAT":
                chatMessageHandler.handleChatMessage(session, messageMap);
                break;
//            case "ALERT":
//            	alertMessageHandler.handleAlertMessage(session, messageMap);
//            	break;
            default:
                logger.warn("Unhandled message type: " + type);
                break;
        }
    }
    

    // 연결이 끊겼을 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session); // 세션 제거
        logger.info("WebSocket connection closed: " + session.getId());
    }

    // 모든 클라이언트에게 메시지를 전송하는 메서드
    public void sendNotificationToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.error("Failed to send message to all clients", e);
                }
            }
        }
    }

    // 특정 클라이언트에게 메시지를 전송하는 메서드
    public void sendMessageToUser(Map<String,String> alertMap) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen() && alertMap.get("emp_id").equals(session.getAttributes().get("emp_id"))) {
                try {
                    session.sendMessage(new TextMessage(alertMap.get("msg")));
                } catch (IOException e) {
                    logger.error("Failed to send message to user: " + alertMap.get("emp_id"), e);
                }
            }
        }
    }

    // 특정 그룹에게 메시지를 전송하는 메서드
    public void sendMessageToGroup(String groupId, String message) {
        // 그룹에 속한 사용자들에게 메시지를 보내는 로직을 추가
    }
}