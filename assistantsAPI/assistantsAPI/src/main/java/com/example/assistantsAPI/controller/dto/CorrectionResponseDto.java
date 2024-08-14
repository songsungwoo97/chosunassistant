package com.example.assistantsAPI.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
public class CorrectionResponseDto {

    @JsonProperty("result")
    private String rawResult;

    @Getter
    @Setter
    public static class InnerResult {
        @JsonProperty("result")
        private List<CorrectionItem> items;
    }

    @Data
    public static class CorrectionItem {
        private String reporter;
        @JsonProperty("arc_id")
        private String arcId;
        private String title;
        private String reqType;
        private String content;
        private String uuid;
    }
}
