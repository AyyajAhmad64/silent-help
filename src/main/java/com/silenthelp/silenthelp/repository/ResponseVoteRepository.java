package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.ResponseVote;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseVoteRepository extends JpaRepository<ResponseVote, Long> {
    boolean existsByResponseAndVoter(Response response, User voter);
    long countByResponseStudent(User user);
}
