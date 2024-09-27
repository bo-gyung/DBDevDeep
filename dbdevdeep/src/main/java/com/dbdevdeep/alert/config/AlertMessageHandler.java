package com.dbdevdeep.alert.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.dbdevdeep.alert.domain.Alert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AlertMessageHandler {

	private final ObjectMapper objectMapper;

	public String sendAlertMessageToUser(Alert alert) {
		// mapper 생성
		ObjectMapper objectMapper = new ObjectMapper();

		// 부모 node 생성
		ObjectNode root = objectMapper.createObjectNode();

		// 자식 node 생성
		ObjectNode child = objectMapper.createObjectNode();
		// 자식 key, value 추가
		child.put("alarm_no", alert.getAlarmNo());
		child.put("alarm_title", alert.getAlarmTitle());
		child.put("alarm_content", alert.getAlarmContent());
		child.put("alarm_time", alert.getAlarmTime().toString());
		child.put("alarm_status", alert.getAlarmStatus());
		child.put("reference_name", alert.getReferenceName());

		// 자식을 부모에 추가
		root.set("alert", child);

		String jsonString = null;
		
		try {
			// json 형식으로 변형
			jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}
}
