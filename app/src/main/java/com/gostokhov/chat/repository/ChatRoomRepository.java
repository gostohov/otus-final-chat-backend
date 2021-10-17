package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.ChatRoom;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ChatRoomRepository extends CassandraRepository<ChatRoom, String> {}
