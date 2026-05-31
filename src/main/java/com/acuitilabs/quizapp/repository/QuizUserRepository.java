package com.acuitilabs.quizapp.repository;

import com.acuitilabs.quizapp.model.QuizUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface QuizUserRepository extends JpaRepository<QuizUser, Integer> {
    Optional<QuizUser> findByEmail(String email);

    // Primary sorting by high scores. Secondary sorting checks for minimum wrong answers chosen.
    @Query("SELECT u FROM QuizUser u ORDER BY u.score DESC, u.wrongAnswersCount ASC, u.completedAt ASC")
    List<QuizUser> getCustomLeaderboard();
}