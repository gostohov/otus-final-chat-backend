package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.dto.instantMessage.InstantMessageDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.repository.InstantMessageRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import com.gostokhov.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
	private final ModelMapper modelMapper;
	private final SimpMessagingTemplate webSocketMessagingTemplate;
	private final UserService userService;

	@Override
	@Transactional
	public void sendMessage(InstantMessageDto instantMessageDto) throws UserNotFoundException {
//		InstantMessage instantMessage = new InstantMessage(
//				instantMessageDto.getChatRoomId(),
//				instantMessageDto.getAuthorUsername(),
//				instantMessageDto.getRecipientUsername(),
//				instantMessageDto.getContent()
//		);
//		ChatRoom chatRoom = chatRoomService.findChatRoomById(instantMessage.getChatRoomId());
//		if (Objects.isNull(chatRoom)) {
//			chatRoom = chatRoomService.createPrivateChatRoom(instantMessage.getRecipientUsernameList());
//		}
//		instantMessage.setChatRoomId(chatRoom.getId());
//		chatRoom.setLastTimeUpdated(instantMessage.getDate());
//		chatRoomService.updateChatRoom(chatRoom);
//
//		InstantMessage message = instantMessageRepository.save(instantMessage);
//		InstantMessageDto messageDto = modelMapper.map(message, InstantMessageDto.class);
//
//		if (chatRoom.getUsers() != null) {
//			chatRoom.getUsers()
//					.stream()
//					.map(User::getUsername)
//					.forEach(username -> webSocketMessagingTemplate.convertAndSendToUser(username, "/queue/messages", messageDto));
//		} else {
//			webSocketMessagingTemplate.convertAndSendToUser(instantMessage.getAuthorUsername(), "/queue/messages", messageDto);
//			webSocketMessagingTemplate.convertAndSendToUser(instantMessage.getRecipientUsernameList(), "/queue/messages", messageDto);
//		}
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
		InstantMessageDto instantMessageDto = modelMapper.map(instantMessage, InstantMessageDto.class);
		for (String username : usernameList) {
			webSocketMessagingTemplate.convertAndSendToUser(username, "/queue/messages", instantMessageDto);
		}
	}

	@Override
	public List<InstantMessage> findInstantMessageListByChatRoomId(Long chatRoomId) {
		return instantMessageRepository.findInstantMessagesByChatRoomId(chatRoomId);
	}
}
