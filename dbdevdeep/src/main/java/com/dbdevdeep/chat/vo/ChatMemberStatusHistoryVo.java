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
public class ChatMemberStatusHistoryVo {
	private Long history_no;
	private Long room_no;
	private String member_id;
	private int member_status;
	private LocalDateTime change_time;
	private String change_by_id;
}
