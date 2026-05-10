package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Report;
import com.silenthelp.silenthelp.model.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @EntityGraph(attributePaths = "reporter")
    Page<Report> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(ReportStatus status);
}
