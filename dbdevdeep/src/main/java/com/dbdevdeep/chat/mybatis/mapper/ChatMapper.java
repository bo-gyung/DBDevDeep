package com.dbdevdeep.chat.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dbdevdeep.chat.dto.CustomChatRoomDto;
import com.dbdevdeep.chat.vo.ChatMemberInfoVo;
import com.dbdevdeep.chat.vo.ChatMemberStatusHistoryVo;
import com.dbdevdeep.chat.vo.ChatMsgVo;
import com.dbdevdeep.chat.vo.ChatRoomVo;

@Mapper
public interface ChatMapper {
	
	// 사용자가 참여중인 채팅방 목록 조회
	List<CustomChatRoomDto> findAllByfromIdAndtoId(@Param("emp_id") String empId);
	
	// 사용자가 참여하고 있는 채팅방의 다른 참여자의 정보를 조회
	List<String> otherMemberIds(Map<String, Object> params);

	// 일대일 채팅방 존재 여부 탐색
	int selectPrivateChatRoom(@Param("admin_id") String admin_id,@Param("emp_id") String emp_id);
	
	// 단체 채팅방 존재 여부 탐색
	int selectGroupChatRoom(@Param("admin_id") String adminId, 
							@Param("emp_id_list") List<String> empIdList, 
							@Param("empCount") int empCount);
	
	// 일대일 채팅방 생성
	int createPrivateChatRoom(ChatRoomVo chatRoomVo);
	
	// 일대일 채팅방 참여 정보 생성
	int createChatMemberInfo(ChatMemberInfoVo memberCmiVo);
	
	// 일대일 채팅 참여자 상태이력 생성
	int createChatMemberStatusHistory(ChatMemberStatusHistoryVo cmshVo);
	
	// 채팅방 이름 조회
	String selectChatRoomName(ChatMemberInfoVo cmiVo);
	
	// 메세지 리스트 조회
	List<ChatMsgVo> selectChatMsgList(@Param("room_no") int room_no, @Param("emp_id") String emp_id);
	
	// 상태이력 리스트 조회
	List<ChatMemberStatusHistoryVo> selectHistoryList(@Param("room_no") int room_no, @Param("emp_id") String emp_id);
	
	// 채팅 매세지 생성
	int createChatMsg(ChatMsgVo vo);
	
	// 채팅 이미지 생성
	int createChatPic(ChatMsgVo vo);
	
	// 채팅 메세지 조회
	ChatMsgVo selectChatMsgVo(@Param("msg_no") int msg_no);
	
	// 채팅방 정보 변경(last_chat, last_time)
	int updateChatRoom(ChatMsgVo vo);
	
	// 메인페이지 헤더에 표시될 채팅 읽음 확인 개수 조회
	int countChatReadCheckByEmpId(@Param("emp_id") String emp_id);
	
	// 참여중인 채팅방 리스트에 표시될 채팅 읽음 확인 개수 조회
	int countChatReadCheckByEmpIdAndRoomNo(Map<String, Object> params);
	
	// 참여중인 채팅방에서 사용자가 읽지 않은 메세지 조회
	List<ChatMsgVo> selectUnreadCheck(@Param("room_no") int room_no, @Param("emp_id") String emp_id);
	
	// 읽음처리
	int createChatReadCheck(@Param("msg_no") int msg_no, @Param("read_id") String read_id);
	
	// 메세지의 읽음확인 개수 출력
	int countChatReadCheckByMsgNo(@Param("msg_no") int msg_no);
	
	// 채팅방에 참여중인 전체 인원수(정원) 구하기
	int headCountByRoomNo(@Param("room_no") int room_no);
	
}
