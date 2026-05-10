package com.silenthelp.silenthelp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_views", uniqueConstraints = {
        @UniqueConstraint(name = "uk_request_view_user", columnNames = {"request_id", "viewer_id"})
})
public class RequestView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private HelpRequest helpRequest;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    private User viewer;

    @Column(nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();

    public void setHelpRequest(HelpRequest helpRequest) {
        this.helpRequest = helpRequest;
    }

    public void setViewer(User viewer) {
        this.viewer = viewer;
    }
}
