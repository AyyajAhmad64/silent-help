package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.ChatMessage;
import com.silenthelp.silenthelp.model.Conversation;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    long countByConversationInAndSenderNotAndReadStatusFalse(List<Conversation> conversations, User sender);

    List<ChatMessage> findByConversationAndSenderNotAndReadStatusFalse(Conversation conversation, User sender);
}
