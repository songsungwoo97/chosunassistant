package com.example.assistantsAPI.service;

import com.example.assistantsAPI.controller.dto.CorrectionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantsAPIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MailService mailService;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public ResponseEntity<String> analyzeComment(String commentData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", commentData);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<CorrectionResponseDto> responseEntity = restTemplate.postForEntity(fastApiUrl + "/analyze_comment", request, CorrectionResponseDto.class);

            log.info("Received response from FastAPI: {}", responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {

                CorrectionResponseDto correctionResponse = responseEntity.getBody();
                if (correctionResponse != null) {

                    List<CorrectionResponseDto.CorrectionItem> items = objectMapper.readValue(correctionResponse.getRawResult(), CorrectionResponseDto.InnerResult.class).getItems();

                    for (CorrectionResponseDto.CorrectionItem item : items) {
                        try {
                            mailService.sendMessage(item);
                        } catch (MessagingException | UnsupportedEncodingException e) {
                            log.error("Failed to send email for item: " + item.getArcId(), e);
                        }
                    }
                    return ResponseEntity.ok("All corrections processed and emails sent.");
                } else {
                    log.warn("Received null response from FastAPI");
                    return ResponseEntity.ok("No corrections to process.");
                }
            } else {
                return ResponseEntity.status(responseEntity.getStatusCode())
                        .body("Error from FastAPI: " + responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("Error processing FastAPI response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing FastAPI response: " + e.getMessage());
        }
    }
}
