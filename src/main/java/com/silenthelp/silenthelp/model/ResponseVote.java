package com.silenthelp.silenthelp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "response_votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_response_vote_user", columnNames = {"response_id", "voter_id"})
})
public class ResponseVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    private Response response;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id")
    private User voter;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }
}
