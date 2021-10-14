package com.gostokhov.chat.service;

import com.gostokhov.chat.domain.ChatRoom;
import com.gostokhov.chat.domain.InstantMessage;

import java.util.List;

public interface ChatRoomService {
    ChatRoom save(ChatRoom chatRoom);
    ChatRoom findById(String chatRoomId);
    List<ChatRoom> findAll();
    void sendPublicMessage(InstantMessage instantMessage);
}
