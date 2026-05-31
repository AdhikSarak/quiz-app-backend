package com.acuitilabs.quizapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_users")
public class QuizUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private BigDecimal score = BigDecimal.ZERO;

    @Column(name = "wrong_answers_count")
    private Integer wrongAnswersCount = 0;

    @Column(name = "has_played")
    private Boolean hasPlayed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt = LocalDateTime.now();

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getWrongAnswersCount() {
        return wrongAnswersCount;
    }

    public void setWrongAnswersCount(Integer wrongAnswersCount) {
        this.wrongAnswersCount = wrongAnswersCount;
    }

    public Boolean getHasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(Boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}