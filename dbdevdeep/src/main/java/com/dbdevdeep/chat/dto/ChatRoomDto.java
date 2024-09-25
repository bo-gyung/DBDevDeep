package com.dbdevdeep.chat.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.dbdevdeep.chat.domain.ChatRoom;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatRoomDto {
	
	private Long room_no;
	private String last_chat;
	@DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
	private LocalDateTime last_time;
	
	public ChatRoom toEntity() {
		return ChatRoom.builder()
				.roomNo(room_no)
				.lastChat(last_chat)
				.lastTime(last_time)
				.build();
	}
	
	public ChatRoomDto toDto(ChatRoom cr) {
		return ChatRoomDto.builder()
				.room_no(cr.getRoomNo())
				.last_chat(cr.getLastChat())
				.last_time(cr.getLastTime())
				.build();
	}
}
