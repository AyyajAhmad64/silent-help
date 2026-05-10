package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Response;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    @EntityGraph(attributePaths = {"helpRequest", "helpRequest.category"})
    List<Response> findByStudentOrderByCreatedAtDesc(User student);

    long countByHiddenFalse();

}
