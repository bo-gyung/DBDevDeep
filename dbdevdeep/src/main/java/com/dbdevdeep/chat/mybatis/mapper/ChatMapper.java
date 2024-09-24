package com.dbdevdeep.chat.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.vo.ChatMemberInfoVo;
import com.dbdevdeep.chat.vo.ChatMemberStatusHistoryVo;
import com.dbdevdeep.chat.vo.ChatRoomVo;

@Mapper
public interface ChatMapper {
	
	// 사용자가 참여중인 채팅방 목록 조회
	List<CustomChatRoomDto> findAllByfromIdAndtoId(@Param("emp_id") String empId);
	
	// 사용자가 참여하고 있는 채팅방의 다른 참여자의 정보를 조회
	String otherMemberPic(Map<String, Object> params);

	// 일대일 채팅방 존재 여부 탐색
	int selectPrivateChatRoom(@Param("admin_id") String admin_id,@Param("emp_id") String emp_id);
	
	// 일대일 채팅방 생성
	int createPrivateChatRoom(ChatRoomVo chatRoomVo);
	
	// 일대일 채팅방 참여 정보 생성
	int createChatMemberInfo(ChatMemberInfoVo memberCmiVo);
	
	// 일대일 채팅 참여자 상태이력 생성
	int createChatMemberStatusHistory(ChatMemberStatusHistoryVo cmshVo);
	
}
