package com.gostokhov.chat.entites;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table
@RequiredArgsConstructor
public class UserChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column(name = "chat_room_id", insertable = false, updatable = false)
    private Long chatRoomId;

    public UserChatRoom(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
