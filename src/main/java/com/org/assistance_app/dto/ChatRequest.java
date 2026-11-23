package com.org.assistance_app.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(

        @NotBlank(message = "Question cannot be blank")
        String question

) {}