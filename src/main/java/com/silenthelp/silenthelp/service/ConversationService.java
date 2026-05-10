package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.model.ChatMessage;
import com.silenthelp.silenthelp.model.Conversation;
import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.ChatMessageRepository;
import com.silenthelp.silenthelp.repository.ConversationRepository;
import com.silenthelp.silenthelp.repository.HelpRequestRepository;
import com.silenthelp.silenthelp.repository.ResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final HelpRequestRepository helpRequestRepository;
    private final ResponseRepository responseRepository;
    private final NotificationService notificationService;

    public ConversationService(ConversationRepository conversationRepository,
                               ChatMessageRepository chatMessageRepository,
                               HelpRequestRepository helpRequestRepository,
                               ResponseRepository responseRepository,
                               NotificationService notificationService) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.helpRequestRepository = helpRequestRepository;
        this.responseRepository = responseRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Conversation startWithRequester(Long requestId, User user) {
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        if (request.getStudent().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You cannot start a private chat with yourself.");
        }
        if (request.isAnonymous()) {
            throw new IllegalArgumentException("This request is anonymous, so direct private chat is not available from the request page.");
        }
        return conversationRepository.findByHelpRequestAndHelper(request, user).orElseGet(() -> {
            Conversation conversation = new Conversation();
            conversation.setHelpRequest(request);
            conversation.setRequester(request.getStudent());
            conversation.setHelper(user);
            return conversationRepository.save(conversation);
        });
    }

    @Transactional
    public Conversation startFromResponse(Long requestId, Long responseId, User user) {
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        Response response = responseRepository.findById(responseId).orElseThrow();
        if (!response.getHelpRequest().getId().equals(request.getId())) {
            throw new IllegalArgumentException("Response does not belong to this request.");
        }
        User requester = request.getStudent();
        User helper = response.getStudent();
        if (!requester.getId().equals(user.getId()) && !helper.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only the requester and responder can open this private chat.");
        }
        return conversationRepository.findByHelpRequestAndHelper(request, helper).orElseGet(() -> {
            Conversation conversation = new Conversation();
            conversation.setHelpRequest(request);
            conversation.setRequester(requester);
            conversation.setHelper(helper);
            return conversationRepository.save(conversation);
        });
    }

    @Transactional(readOnly = true)
    public List<Conversation> forUser(User user) {
        return conversationRepository.findByRequesterOrHelperOrderByUpdatedAtDesc(user, user);
    }

    @Transactional
    public Conversation accessible(Long id, User user) {
        Conversation conversation = conversationRepository.findDetailedById(id).orElseThrow();
        if (!conversation.includes(user)) {
            throw new IllegalArgumentException("You cannot access this private chat.");
        }
        chatMessageRepository.findByConversationAndSenderNotAndReadStatusFalse(conversation, user)
                .forEach(message -> message.setReadStatus(true));
        return conversation;
    }

    @Transactional
    public void send(Long id, String message, User sender) {
        Conversation conversation = accessible(id, sender);
        String cleaned = message == null ? "" : message.trim();
        if (cleaned.length() < 2 || cleaned.length() > 2000) {
            throw new IllegalArgumentException("Message must be between 2 and 2000 characters.");
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversation(conversation);
        chatMessage.setSender(sender);
        chatMessage.setMessage(cleaned);
        chatMessageRepository.save(chatMessage);
        conversation.touch();
        User recipient = conversation.otherParticipant(sender);
        notificationService.notify(recipient, "New private message",
                sender.getDisplayName() + " sent you a private message about: " + conversation.getHelpRequest().getTitle(),
                "/chats/" + conversation.getId());
    }

    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        List<Conversation> conversations = forUser(user);
        if (conversations.isEmpty()) {
            return 0;
        }
        return chatMessageRepository.countByConversationInAndSenderNotAndReadStatusFalse(conversations, user);
    }
}
