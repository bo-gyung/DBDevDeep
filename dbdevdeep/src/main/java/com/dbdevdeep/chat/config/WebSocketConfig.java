package com.dbdevdeep.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

//채팅 웹소켓 설정 클래스!
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	/// 웹소켓 핸들러 의존성 주입
	private final WebSocketHandler webSocketHandler;
	@Autowired
	public WebSocketConfig (WebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}
	
	// 환경설정 관련된 함수 오버라이드
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 클라이언트에서 WebSocket 연결을 맺을 때 사용할 엔드포인트 설정
		registry
			.addHandler(webSocketHandler, "/ws")
			.addInterceptors(new CustomHandshakeInterceptor()) // 인터셉터 추가
			.setAllowedOrigins("*");
		
	}

}
