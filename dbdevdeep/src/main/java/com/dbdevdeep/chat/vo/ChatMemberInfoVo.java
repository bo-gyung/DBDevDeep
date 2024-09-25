package com.dbdevdeep.chat.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatMemberInfoVo {
	private int info_no;
	private String member_id;
	private int room_no;
	private String room_name;
	private LocalDateTime join_time;
	private int is_admin;
}
