package com.dbdevdeep.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {



}
