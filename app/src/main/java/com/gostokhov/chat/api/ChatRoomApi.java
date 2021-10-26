package com.gostokhov.chat.api;

import com.gostokhov.chat.dto.chatRoom.ChatRoomDto;
import com.gostokhov.chat.dto.instantMessage.InstantMessageDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.InstantMessage;
import com.gostokhov.chat.exception.ExceptionHandling;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.InstantMessageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/", "/chatroom"})
public class ChatRoomApi extends ExceptionHandling {
    private final ChatRoomService chatRoomService;
    private final ModelMapper modelMapper;
    private final InstantMessageService instantMessageService;

    @GetMapping("/find-by-user")
    public ResponseEntity<List<ChatRoomDto>> findChatRoomByUserIds(@RequestParam List<Long> ids) throws UserNotFoundException {
        List<ChatRoom> chatRoomList = chatRoomService.findChatRoomListByUserIds(ids);
        List<ChatRoomDto> chatRoomDto = chatRoomList.stream()
                                                    .map(chatRoom -> modelMapper.map(chatRoom, ChatRoomDto.class))
                                                    .collect(Collectors.toList());
        return new ResponseEntity<>(chatRoomDto, OK);
    }

    @GetMapping("/message-list")
    public ResponseEntity<List<InstantMessageDto>> findInstantMessageListByChatRoomId(@RequestParam Long crId) {
        List<InstantMessage> instantMessageList = instantMessageService.findInstantMessageListByChatRoomId(crId);
        List<InstantMessageDto> instantMessageDtoList = instantMessageList.stream()
                                                                          .map(im -> modelMapper.map(im, InstantMessageDto.class))
                                                                          .collect(Collectors.toList());
        return new ResponseEntity<>(instantMessageDtoList, OK);
    }

    @GetMapping("/find-by-id")
    public ResponseEntity<ChatRoomDto> findChatRoomByUserIds(@RequestParam Long id) throws UserNotFoundException {
        ChatRoom chatRoom = chatRoomService.findChatRoomById(id);
        ChatRoomDto chatRoomDto = modelMapper.map(chatRoom, ChatRoomDto.class);
        return new ResponseEntity<>(chatRoomDto, OK);
    }

    @MessageMapping("/send.message")
    public void sendMessage(@Payload InstantMessageDto instantMessageDto) throws UserNotFoundException {
        instantMessageService.sendMessage(instantMessageDto);
//        InstantMessage message = instantMessageService.appendInstantMessageToConversations(instantMessageDto);
//        InstantMessageDto messageDto = modelMapper.map(message, InstantMessageDto.class);
//        ChatRoom chatRoom = chatRoomService.findChatRoomById(message.getChatRoomId());
//        chatRoom.getUsers()
//                .stream()
//                .map(User::getUsername)
//                .forEach(username -> webSocketMessagingTemplate.convertAndSendToUser(username, "/queue/messages", messageDto));
    }

    @SubscribeMapping("/old.messages/{chatRoomId}")
    public List<InstantMessage> findAllInstantMessagesByChatRoomId(@DestinationVariable("chatRoomId") Long chatRoomId) {
        return null;
    }
}
