package com.org.assistance_app.dto;

import java.util.List;

public record ChatResponse(
        String question,
        String answer,
        List<SourceReference> sources
) {}