package com.dbdevdeep.chat.vo;

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
public class ChatReadCheckVo {
	private int check_no;
	private int msg_no;
	private String read_id;
	private LocalDateTime read_time;
}
