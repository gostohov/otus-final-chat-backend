package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.InstantMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface InstantMessageRepository extends CassandraRepository<InstantMessage, Long> {
	List<InstantMessage> findInstantMessagesByChatRoomId(Long chatRoomId);
}
