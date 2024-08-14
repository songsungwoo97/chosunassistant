package com.example.assistantsAPI.controller;

import com.example.assistantsAPI.controller.dto.CommentDataRequestDto;
import com.example.assistantsAPI.service.AssistantsAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AssistantsAPIController {

    private final AssistantsAPIService assistantsAPIService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeComment(@RequestBody String commentData) {
        return assistantsAPIService.analyzeComment(commentData);
    }

}
