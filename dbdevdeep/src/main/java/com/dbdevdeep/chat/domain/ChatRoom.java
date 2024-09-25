package com.dbdevdeep.chat.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="chat_room")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class ChatRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="room_no")
	private Long roomNo;
	
	@Column(name="last_chat")
	private String lastChat;
	
	@Column(name="last_time")
	private LocalDateTime lastTime;
	
	// 채팅 참여자 정보
	@OneToMany(mappedBy = "chatRoom")
	private List<ChatMemberInfo> chatMemberInfo;
	
	// 채팅 참여자 상태이력
	@OneToMany(mappedBy = "chatRoom")
	private List<ChatMemberStatusHistory> chatMemberStatusHistory;
		

}
