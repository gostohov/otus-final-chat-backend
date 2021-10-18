package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;
import com.gostokhov.chat.repository.ChatRoomRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.utility.Destinations;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate webSocketMessagingTemplate;

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
        return new ChatRoom(); // ref need
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
