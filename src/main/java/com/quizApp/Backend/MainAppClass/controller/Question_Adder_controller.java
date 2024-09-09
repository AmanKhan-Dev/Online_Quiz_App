package com.quizApp.Backend.MainAppClass.controller;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quizApp.Backend.MainAppClass.Service.Question_Adder_Service;
import com.quizApp.Backend.MainAppClass.model.Question_Adder;
import com.quizApp.Backend.MainAppClass.model.Student;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/codingQuestions")
public class Question_Adder_controller {
    @Autowired
    Question_Adder_Service qService;

    @PostMapping("/add")
    public ResponseEntity<String> addNewQuestion(@Valid @RequestBody Question_Adder questionAdder) {
        try {
          qService.insertIfNotExists(questionAdder);
            
            return new ResponseEntity<>("Question added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle exceptions (e.g., database issues)
            return new ResponseEntity<>("Error adding question: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("all")
    public List<Question_Adder> getAllQuestions() {
        return qService.getAllQuestions();
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateQuestion(@RequestBody Question_Adder question_Adder){
           qService.updateQuestion(question_Adder);
        return ResponseEntity.ok("Question updated successfully");
    }

    @GetMapping("/allCodingQuestions")
public ResponseEntity<List<Map<String, Object>>> getQuestionsBySetId(
        @RequestParam("questionSetId") String questionSetId) {
    
    List<Map<String, Object>> questions = qService.getQuestionsBySetId(questionSetId);
    if (!questions.isEmpty()) {
        return ResponseEntity.ok(questions);
    } else {
        return ResponseEntity.notFound().build();
    }
}


  @GetMapping("/questions/{questionSetId}/{questionNo}")
public ResponseEntity<Question_Adder> getQuestionBySetIdAndNo(
        @PathVariable("questionSetId") String questionSetId,
        @PathVariable("questionNo") int questionNo) {
    
    Question_Adder question = qService.getQuestionBySetIdAndNo(questionSetId, questionNo);
    
    if (question != null) {
        return ResponseEntity.ok(question);
    } else {
        return ResponseEntity.notFound().build();
    }
}

 @GetMapping("/findByEmail")
    public ResponseEntity<Student> findStudentByEmail(@RequestParam String email) {
        Optional<Student> studentOptional = qService.findStudentByEmail(email);
        return studentOptional.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getDefaultMessage())
                                .findFirst()
                                .orElse("Invalid input");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
