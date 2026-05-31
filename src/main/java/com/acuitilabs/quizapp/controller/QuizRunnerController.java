package com.acuitilabs.quizapp.controller;

import com.acuitilabs.quizapp.model.Question;
import com.acuitilabs.quizapp.model.QuizUser;
import com.acuitilabs.quizapp.repository.QuestionRepository;
import com.acuitilabs.quizapp.repository.QuizUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v2")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QuizRunnerController {

    @Autowired private QuizUserRepository userRepository;
    @Autowired private QuestionRepository questionRepository;

    // Cache tracking mapped lists of usernames who correctly evaluated specific question IDs
    private static final Map<Integer, List<String>> roundCorrectUsersMap = new ConcurrentHashMap<>();

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email) {
        Optional<QuizUser> user = userRepository.findByEmail(email);
        return ResponseEntity.ok(Map.of("hasPlayed", user.isPresent() && user.get().getHasPlayed()));
    }

    @PostMapping("/submit-answer")
    public ResponseEntity<?> recordAnswer(@RequestParam String email, @RequestParam Integer questionId, @RequestParam String option, @RequestParam String name) {
        Question q = questionRepository.findById(questionId).orElse(null);
        if (q == null) return ResponseEntity.badRequest().body("Question element reference missing.");

        boolean isCorrect = option.equalsIgnoreCase(q.getCorrectOption());
        if (isCorrect) {
            roundCorrectUsersMap.computeIfAbsent(questionId, k -> Collections.synchronizedList(new ArrayList<>())).add(name);
        }
        return ResponseEntity.ok(Map.of("correct", isCorrect));
    }

    @GetMapping("/intermission-stats")
    public ResponseEntity<?> getStats(@RequestParam Integer questionId) {
        List<String> correctUsers = roundCorrectUsersMap.getOrDefault(questionId, new ArrayList<>());
        return ResponseEntity.ok(Map.of(
                "correctUsers", correctUsers,
                "leaderboard", userRepository.getCustomLeaderboard()
        ));
    }

    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeQuiz(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        Map<String, String> answers = (Map<String, String>) payload.get("answers");

        List<Question> questions = questionRepository.findAll();
        double runningScore = 0.0;
        int wrongCount = 0;

        for (Question q : questions) {
            String submitted = answers.get(String.valueOf(q.getId()));
            if (submitted != null && submitted.equalsIgnoreCase(q.getCorrectOption())) {
                runningScore += 1.0;
            } else {
                runningScore -= 0.2; // Correct = +1.0, Wrong/Skipped = -0.2
                wrongCount++;
            }
        }

        QuizUser user = userRepository.findByEmail(email).orElse(new QuizUser());
        user.setEmail(email);
        user.setDisplayName(name);
        user.setScore(BigDecimal.valueOf(runningScore));
        user.setWrongAnswersCount(wrongCount);
        user.setHasPlayed(true);
        user.setCompletedAt(LocalDateTime.now());
        userRepository.save(user);

        // Round the double to 2 decimal places using Math.round
        double cleanScore = Math.round(runningScore * 100.0) / 100.0;

// Save using the clean score
        user.setScore(BigDecimal.valueOf(cleanScore));
// ... previous setters

        userRepository.save(user);

// Return the cleanScore back to the frontend instead of runningScore
        return ResponseEntity.ok(Map.of("score", cleanScore, "leaderboard", userRepository.getCustomLeaderboard()));
    }
}