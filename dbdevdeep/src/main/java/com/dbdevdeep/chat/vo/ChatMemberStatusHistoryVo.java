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
	private int history_no;
	private int room_no;
	private String member_id;
	private int member_status;
	private LocalDateTime change_time;
	private String changed_by_id;
}
