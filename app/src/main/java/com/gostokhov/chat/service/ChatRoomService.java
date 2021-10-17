package com.gostokhov.chat.service;

import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;

import java.util.List;

public interface ChatRoomService {
    ChatRoom save(ChatRoom chatRoom);
    ChatRoom findById(String chatRoomId);
    List<ChatRoom> findAll();
    void sendPublicMessage(InstantMessage instantMessage);
}
