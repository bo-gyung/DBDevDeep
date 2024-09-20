 package com.dbdevdeep.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dbdevdeep.chat.service.ChatService;

@Controller
public class ChatApiController {
	private final ChatService chatService;
	
	@Autowired
	public ChatApiController(ChatService chatService) {
		this.chatService = chatService;
	}
}
