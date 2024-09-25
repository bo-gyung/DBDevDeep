package com.dbdevdeep.chat.dto;

import java.time.LocalDateTime;

import com.dbdevdeep.chat.domain.ChatMemberInfo;
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
public class ChatMemberInfoDto {
	
	private Long info_no;
	private String member_id;
	private Long room_no;
	private String room_name;
	private LocalDateTime join_time;
	private int is_admin;
	
	public ChatMemberInfo toEntity(Employee member, ChatRoom cr) {
		return ChatMemberInfo.builder()
				.infoNo(info_no)
				.employee(member)
				.chatRoom(cr)
				.roomName(room_name)
				.joinTime(join_time)
				.isAdmin(is_admin)
				.build();
	}
	
	public ChatMemberInfoDto toDto(ChatMemberInfo cmi) {
		return ChatMemberInfoDto.builder()
				.info_no(cmi.getInfoNo())
				.member_id(cmi.getEmployee().getEmpId())
				.room_no(cmi.getChatRoom().getRoomNo())
				.room_name(cmi.getRoomName())
				.join_time(cmi.getJoinTime())
				.is_admin(cmi.getIsAdmin())
				.build();
	}
	
}
