package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {}
