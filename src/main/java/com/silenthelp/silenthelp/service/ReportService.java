package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.dto.ReportForm;
import com.silenthelp.silenthelp.model.Report;
import com.silenthelp.silenthelp.model.ReportStatus;
import com.silenthelp.silenthelp.model.ReportTargetType;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void report(ReportTargetType targetType, Long targetId, ReportForm form, User reporter) {
        Report report = new Report();
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(form.getReason());
        report.setDetails(form.getDetails());
        report.setReporter(reporter);
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Page<Report> all(Pageable pageable) {
        return reportRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public void updateStatus(Long id, ReportStatus status) {
        Report report = reportRepository.findById(id).orElseThrow();
        report.setStatus(status);
    }

    @Transactional(readOnly = true)
    public long openCount() {
        return reportRepository.countByStatus(ReportStatus.OPEN);
    }
}
