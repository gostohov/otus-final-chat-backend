package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.dto.chatRoom.ChatRoomCreateDto;
import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.entites.UserChatRoom;
import com.gostokhov.chat.enumiration.ChatRoomType;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.repository.ChatRoomRepository;
import com.gostokhov.chat.repository.UserChatRoomRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    @Override
    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDto chatRoomCreateDto) throws UserNotFoundException {
        Set<User> users = userService.validateUsernameList(chatRoomCreateDto.getUsernameList());
        ChatRoomType chatRoomType = chatRoomCreateDto.getUsernameList().size() > 2 ? ChatRoomType.GROUP : ChatRoomType.PRIVATE;
        ChatRoom chatRoom = chatRoomRepository.save(
                ChatRoom.builder()
                        .name(chatRoomCreateDto.getName())
                        .description(chatRoomCreateDto.getDescription())
                        .type(chatRoomType)
                        .build()
        );
        createUserChatRoomEntities(chatRoom, users);
        return chatRoom;
    }

    private void createUserChatRoomEntities(ChatRoom chatRoom, Set<User> users) {
        for (User user : users) {
            userChatRoomRepository.save(new UserChatRoom(user, chatRoom));
        }
    }

    @Override
    @Transactional
    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> findChatRoomListByUserIds(List<Long> userIds) {
        return chatRoomRepository.findByUsersIdIn(userIds);
    }

    @Override
    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepository.findChatRoomById(id);
    }
}
