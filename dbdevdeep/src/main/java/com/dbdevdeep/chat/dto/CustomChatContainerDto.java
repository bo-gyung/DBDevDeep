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
public class CustomChatContainerDto {
	private String type; // me / notMe / history
	private String writer_id;
	private String writer_name;
	private String profilePic;
	private String content;
	private LocalDateTime timestamp;
	// private int readcheck;

}
