package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Conversation;
import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByHelpRequestAndHelper(HelpRequest helpRequest, User helper);

    @EntityGraph(attributePaths = {"helpRequest", "requester", "helper"})
    List<Conversation> findByRequesterOrHelperOrderByUpdatedAtDesc(User requester, User helper);

    @EntityGraph(attributePaths = {"helpRequest", "requester", "helper", "messages", "messages.sender"})
    Optional<Conversation> findDetailedById(Long id);
}
