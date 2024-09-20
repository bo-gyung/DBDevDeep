package com.dbdevdeep.chat.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dbdevdeep.chat.dto.CustomChatRoomDto;

@Mapper
public interface ChatMapper {
	
	// 사용자가 참여중인 채팅방 목록 조회
	List<CustomChatRoomDto> findAllByfromIdAndtoId(@Param("emp_id") String empId);
	
	// 사용자가 참여하고 있는 채팅방의 다른 참여자의 정보를 조회
	String otherMemberPic(Map<String, Object> params);

	
	
}
