package com.acuitilabs.quizapp.repository;

import com.acuitilabs.quizapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // Standard database operations are inherited automatically
}