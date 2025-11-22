package org.acme.dto;

public record ZipCodeResponseDto(
    String zip,
    String city,
    String county,
    String state,
    String timezone,
    String type
) {
}