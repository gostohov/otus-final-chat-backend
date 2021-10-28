package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.dto.chatRoom.ChatRoomCreateDto;
import com.gostokhov.chat.dto.instantMessage.InstantMessageDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;
import com.gostokhov.chat.enumiration.ChatRoomType;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.repository.InstantMessageRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class InstantMessageServiceImpl implements InstantMessageService {

	private final InstantMessageRepository instantMessageRepository;
	private final ChatRoomService chatRoomService;
	private final SimpMessagingTemplate webSocketMessagingTemplate;

	@Override
	@Transactional
	public void sendMessage(InstantMessageDto instantMessageDto) throws UserNotFoundException {
		Set<String> usernameList = instantMessageDto.getRecipientUsernameList();
		InstantMessage instantMessage = new InstantMessage(
				instantMessageDto.getChatRoomId(),
				instantMessageDto.getAuthorUsername(),
				instantMessageDto.getRecipientUsernameList(),
				instantMessageDto.getContent()
		);
		ChatRoom chatRoom = chatRoomService.findChatRoomById(instantMessage.getChatRoomId());
		if (Objects.isNull(chatRoom)) {
			ChatRoomCreateDto chatRoomCreateDto = new ChatRoomCreateDto();
			ChatRoomType chatRoomType = usernameList.size() > 2 ? ChatRoomType.GROUP : ChatRoomType.PRIVATE;
			chatRoomCreateDto.setType(chatRoomType);
			chatRoomCreateDto.setUsernameList(usernameList);
			chatRoom = chatRoomService.createChatRoom(chatRoomCreateDto);
			instantMessage.setChatRoomId(chatRoom.getId());
		}
		chatRoom.setLastTimeUpdated(instantMessage.getDate());
		chatRoomService.updateChatRoom(chatRoom);
		sendMessageToUsernameList(instantMessage);
	}

	@Override
	@Transactional
	public void sendGreetingMessage(ChatRoom chatRoom, Set<String> usernameList) {
		InstantMessage instantMessage = new InstantMessage(
				chatRoom.getId(),
				"system",
				usernameList,
				"Welcome to new chat group: " + chatRoom.getName()
		);
		chatRoom.setLastTimeUpdated(instantMessage.getDate());
		chatRoomService.updateChatRoom(chatRoom);
		sendMessageToUsernameList(instantMessage);

	}

	@Override
	public List<InstantMessage> findInstantMessageListByChatRoomId(Long chatRoomId) {
		return instantMessageRepository.findInstantMessagesByChatRoomId(chatRoomId);
	}

	private void sendMessageToUsernameList(InstantMessage instantMessage) {
		instantMessageRepository.save(instantMessage);
		for (String username : instantMessage.getRecipientUsernameList()) {
			try {
				webSocketMessagingTemplate.convertAndSendToUser(username, "/queue/messages", instantMessage);
			} catch (Exception ignored) {}
		}
	}
}
