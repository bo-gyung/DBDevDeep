package com.dbdevdeep.chat.dto;

import java.time.LocalDateTime;

import com.dbdevdeep.chat.domain.ChatMemberStatusHistory;
import com.dbdevdeep.chat.domain.ChatRoom;
import com.dbdevdeep.employee.domain.Employee;

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
public class ChatMemberStatusHistoryDto {
	
	private Long history_no;
	private Long room_no;
	private String member_id;
	private int member_status;
	private LocalDateTime change_time;
	private String change_by_id;
	
	public ChatMemberStatusHistory toEntity(Employee member, ChatRoom cr, Employee changeBy) {
		return ChatMemberStatusHistory.builder()
				.historyNo(history_no)
				.chatRoom(cr)
				.member(member)
				.memberStatus(member_status)
				.changeTime(change_time)
				.changeBy(changeBy)
				.build();
	}
	
	public ChatMemberStatusHistoryDto toDto(ChatMemberStatusHistory cmsh) {
		return ChatMemberStatusHistoryDto.builder()
				.history_no(cmsh.getHistoryNo())
				.room_no(cmsh.getChatRoom().getRoomNo())
				.member_id(cmsh.getMember().getEmpId())
				.member_status(cmsh.getMemberStatus())
				.change_time(cmsh.getChangeTime())
				.change_by_id(cmsh.getChangeBy().getEmpId())
				.build();
	}
	
}
