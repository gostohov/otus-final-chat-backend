package com.gostokhov.chat.service;

import com.gostokhov.chat.dto.instantMessage.InstantMessageDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;
import com.gostokhov.chat.exception.domain.UserNotFoundException;

import java.util.List;
import java.util.Set;

public interface InstantMessageService {

	List<InstantMessage> findInstantMessageListByChatRoomId(Long chatRoomId);

    void sendMessage(InstantMessageDto instantMessageDto) throws UserNotFoundException;

    void sendGreetingMessage(ChatRoom chatRoom, Set<String> usernameList);
}
