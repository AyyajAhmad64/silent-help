package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.SavedRequest;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedRequestRepository extends JpaRepository<SavedRequest, Long> {
    Optional<SavedRequest> findByHelpRequestAndUser(HelpRequest helpRequest, User user);
    boolean existsByHelpRequestAndUser(HelpRequest helpRequest, User user);

    @EntityGraph(attributePaths = {"helpRequest", "helpRequest.category", "helpRequest.student"})
    List<SavedRequest> findByUserOrderByCreatedAtDesc(User user);
}
