package com.gostokhov.chat.service;

import com.gostokhov.chat.domain.InstantMessage;

import java.util.List;

public interface InstantMessageService {
	void appendInstantMessageToConversations(InstantMessage instantMessage);
	List<InstantMessage> findAllInstantMessagesFor(String chatRoomId);
}
