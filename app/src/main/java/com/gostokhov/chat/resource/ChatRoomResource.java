package com.gostokhov.chat.resource;

import com.gostokhov.chat.domain.InstantMessage;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatRoomResource {

    private final ChatRoomService chatRoomService;
    private final InstantMessageService instantMessageService;

    @MessageMapping("/send.message")
    public void sendMessage(@Payload InstantMessage instantMessage) {
        var temp = chatRoomService.findAll();

        if (instantMessage.isPublic()) {
            chatRoomService.sendPublicMessage(instantMessage);
        }  //            chatRoomService.sendPrivateMessage(instantMessage);

    }

    @SubscribeMapping("/old.messages/{chatRoomId}")
    public List<InstantMessage> listOldMessagesFromUserOnSubscribe(@DestinationVariable("chatRoomId") String chatRoomId) {
        return instantMessageService.findAllInstantMessagesFor(chatRoomId);
    }

}
