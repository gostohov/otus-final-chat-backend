package com.gostokhov.chat.service;

import com.gostokhov.chat.entites.InstantMessage;

import java.util.List;

public interface InstantMessageService {
	void appendInstantMessageToConversations(InstantMessage instantMessage);
	List<InstantMessage> findAllInstantMessagesFor(String chatRoomId);
}
