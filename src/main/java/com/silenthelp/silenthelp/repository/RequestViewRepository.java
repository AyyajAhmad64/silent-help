package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.RequestView;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestViewRepository extends JpaRepository<RequestView, Long> {
    boolean existsByHelpRequestAndViewer(HelpRequest helpRequest, User viewer);
}
