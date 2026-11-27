package com.org.assistance_app.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * SearchRequestDto
 */
public record SearchRequestDto(
        @NotBlank(message = "Query cannot be empty")
        String query,
        Integer topK) {

}
