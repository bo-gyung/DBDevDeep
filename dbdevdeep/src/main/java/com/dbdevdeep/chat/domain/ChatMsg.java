package com.dbdevdeep.chat.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.dbdevdeep.employee.domain.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="chat_msg")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class ChatMsg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="msg_no")
	private Long msgNo;
	
	@ManyToOne
	@JoinColumn(name="room_no")
	private ChatRoom chatRoom;
	
	@ManyToOne
	@JoinColumn(name="writer_id")
	private Employee employee;
	
	@Column(name="chat_content")
	private String chatContent;
	
	@Column(name="is_important")
	private int isImportant;
	
	@Column(name="reg_time", updatable = false)
	private LocalDateTime regTime;
	
	@Column(name="is_edited")
	private int isEdited;
	
	@Column(name="mod_time")
	private LocalDateTime modTime;
	
	@Column(name="ori_pic_name")
	private String oriPicName;
	
	@Column(name="new_pic_name")
	private String newPicName;
	
	// 채팅읽음확인
	@OneToMany(mappedBy = "chatMsg")
	private List<ChatReadCheck> chatReadCheck;
	
}
