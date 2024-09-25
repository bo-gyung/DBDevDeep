package com.dbdevdeep.chat.domain;

import java.time.LocalDateTime;

import com.dbdevdeep.employee.domain.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="chat_member_info", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"member_id", "room_no"})})
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class ChatMemberInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="info_no")
	private Long infoNo;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="room_no")
	private ChatRoom chatRoom;

	@Column(name="room_name")
	private String roomName;
	
	@Column(name="join_time")
	private LocalDateTime joinTime;
	
	@Column(name="is_admin")
	private int isAdmin;
}
