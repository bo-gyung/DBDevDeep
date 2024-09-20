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
public class ChatMsgVo {
	private Long msg_no;
	private Long room_no;
	private String writer_id;
	private String chat_content;
	private int is_important;
	private LocalDateTime reg_time;
	private int is_edited;
	private LocalDateTime mod_time;
	private String ori_pic_name;
	private String new_pic_name;

}
