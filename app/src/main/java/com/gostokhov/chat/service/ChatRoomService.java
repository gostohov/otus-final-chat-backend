package com.gostokhov.chat.service;

import com.gostokhov.chat.dto.chatRoom.ChatRoomCreateDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.exception.domain.UserNotFoundException;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createPrivateChatRoom(String username) throws UserNotFoundException;

    ChatRoom createChatRoom(ChatRoomCreateDto chatRoomCreateDto) throws UserNotFoundException;

    ChatRoom updateChatRoom(ChatRoom chatRoom);

    List<ChatRoom> findChatRoomListByUserIds(List<Long> userIds) throws UserNotFoundException;

    ChatRoom findChatRoomById(Long id);
}
