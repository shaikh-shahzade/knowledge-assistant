package com.org.assistance_app.dto;

import java.util.List;

/**
 * SearchResultDto
 */
public record SearchResultDto(
    String query,
        List<SearchResultItem> results
) {

}
