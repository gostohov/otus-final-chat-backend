package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.domain.ChatRoom;
import com.gostokhov.chat.domain.InstantMessage;
import com.gostokhov.chat.repository.InstantMessageRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstantMessageServiceImpl implements InstantMessageService {

	private InstantMessageRepository instantMessageRepository;

	@Autowired
	public InstantMessageServiceImpl(ChatRoomService chatRoomService, InstantMessageRepository instantMessageRepository) {
		this.instantMessageRepository = instantMessageRepository;
	}

	@Override
	public void appendInstantMessageToConversations(InstantMessage instantMessage) {
//		ChatRoom chatRoom = chatRoomService.findById(instantMessage.getChatRoomId());
		instantMessageRepository.save(instantMessage);
	}

	@Override
	public List<InstantMessage> findAllInstantMessagesFor(String chatRoomId) {
		return instantMessageRepository.findInstantMessagesByChatRoomId(chatRoomId);
	}
}
