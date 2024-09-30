package com.dbdevdeep.chat.dto;

import java.time.LocalDateTime;

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
public class CustomChatRoomDto {
	private int room_no;
	private String room_name;
	private String last_chat;
	private LocalDateTime last_time;
	private String room_pic;

}
