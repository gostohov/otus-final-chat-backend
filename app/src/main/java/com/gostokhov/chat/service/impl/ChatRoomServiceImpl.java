package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.domain.ChatRoom;
import com.gostokhov.chat.domain.InstantMessage;
import com.gostokhov.chat.repository.ChatRoomRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import com.gostokhov.chat.utility.Destinations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private ChatRoomRepository chatRoomRepository;
    private SimpMessagingTemplate webSocketMessagingTemplate;

    @Autowired
    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository, SimpMessagingTemplate webSocketMessagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    // TODO: Тут лучше выкинуть exception
    @Override
    public ChatRoom findById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElse(new ChatRoom());
    }

    // TODO: Лучше сначала записать данные в базу, а потом отправить в топик
    @Override
    public void sendPublicMessage(InstantMessage instantMessage) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(instantMessage.getChatRoomId()),
                instantMessage);

//        instantMessageService.appendInstantMessageToConversations(instantMessage);
    }

}
