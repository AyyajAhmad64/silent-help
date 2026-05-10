package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.HelpRequest;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {
    @EntityGraph(attributePaths = {"category", "student"})
    @Query("""
            select h from HelpRequest h
            where h.hidden = false
              and h.resolved = false
              and (:categorySlug is null or h.category.slug = :categorySlug)
              and (:urgency is null or h.urgency = :urgency)
              and (:status is null or (:status = 'OPEN' and h.resolved = false))
              and (:keyword is null or lower(h.title) like lower(concat('%', :keyword, '%'))
                   or lower(h.description) like lower(concat('%', :keyword, '%'))
                   or lower(h.tags) like lower(concat('%', :keyword, '%')))
            """)
    Page<HelpRequest> searchVisible(@Param("categorySlug") String categorySlug,
                                     @Param("keyword") String keyword,
                                     @Param("urgency") String urgency,
                                     @Param("status") String status,
                                     Pageable pageable);

    @EntityGraph(attributePaths = {"category", "student"})
    @Query("""
            select h from HelpRequest h
            where (:categorySlug is null or h.category.slug = :categorySlug)
              and (:keyword is null or lower(h.title) like lower(concat('%', :keyword, '%'))
                   or lower(h.description) like lower(concat('%', :keyword, '%'))
                   or lower(h.tags) like lower(concat('%', :keyword, '%')))
            """)
    Page<HelpRequest> searchAllForAdmin(@Param("categorySlug") String categorySlug,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);

    @EntityGraph(attributePaths = {"category", "student", "responses", "responses.student"})
    Optional<HelpRequest> findWithCategoryByIdAndHiddenFalse(Long id);

    @EntityGraph(attributePaths = {"category", "student"})
    List<HelpRequest> findTop6ByHiddenFalseAndResolvedFalseOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"category"})
    List<HelpRequest> findByStudentAndResolvedFalseOrderByCreatedAtDesc(User student);

    long countByHiddenFalse();

    long countByResolvedTrue();

    long countByAcceptedResponseStudent(User student);

    long countByStatus(com.silenthelp.silenthelp.model.RequestStatus status);
}
