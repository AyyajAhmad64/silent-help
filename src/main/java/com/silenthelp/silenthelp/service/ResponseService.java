package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.dto.ResponseForm;
import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.ResponseVote;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.HelpRequestRepository;
import com.silenthelp.silenthelp.repository.ResponseRepository;
import com.silenthelp.silenthelp.repository.ResponseVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final HelpRequestRepository helpRequestRepository;
    private final NotificationService notificationService;
    private final ResponseVoteRepository responseVoteRepository;

    public ResponseService(ResponseRepository responseRepository,
                           HelpRequestRepository helpRequestRepository,
                           NotificationService notificationService,
                           ResponseVoteRepository responseVoteRepository) {
        this.responseRepository = responseRepository;
        this.helpRequestRepository = helpRequestRepository;
        this.notificationService = notificationService;
        this.responseVoteRepository = responseVoteRepository;
    }

    @Transactional
    public Response respond(Long requestId, ResponseForm form, User student) {
        String message = form.getMessage() == null ? "" : form.getMessage().trim();
        if (message.isBlank()) {
            throw new IllegalArgumentException("Response cannot be empty.");
        }
        HelpRequest request = helpRequestRepository.findById(requestId).orElseThrow();
        Response response = new Response();
        response.setHelpRequest(request);
        response.setStudent(student);
        response.setMessage(message);
        response.setAnonymous(form.isAnonymous());
        Response saved = responseRepository.save(response);
        request.incrementResponseCount();
        request.setUpdatedAt(LocalDateTime.now());
        if (!request.getStudent().getId().equals(student.getId())) {
            notificationService.notify(request.getStudent(), "New response on your request",
                    "A student replied to: " + request.getTitle(), "/requests/" + request.getId());
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Response> mine(User user) {
        return responseRepository.findByStudentOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void hide(Long id, boolean hidden) {
        Response response = responseRepository.findById(id).orElseThrow();
        response.setHidden(hidden);
    }

    @Transactional
    public boolean markHelpful(Long id, User voter) {
        Response response = responseRepository.findById(id).orElseThrow();
        if (responseVoteRepository.existsByResponseAndVoter(response, voter)) {
            return false;
        }
        ResponseVote vote = new ResponseVote();
        vote.setResponse(response);
        vote.setVoter(voter);
        responseVoteRepository.save(vote);
        response.incrementHelpfulCount();
        if (!response.getStudent().getId().equals(voter.getId())) {
            notificationService.notify(response.getStudent(), "Your reply was marked helpful",
                    "Someone found your response useful on: " + response.getHelpRequest().getTitle(),
                    "/requests/" + response.getHelpRequest().getId());
        }
        return true;
    }

    @Transactional(readOnly = true)
    public long visibleCount() {
        return responseRepository.countByHiddenFalse();
    }

    @Transactional(readOnly = true)
    public long helpfulVotesFor(User user) {
        return responseVoteRepository.countByResponseStudent(user);
    }
}
