package org.acme.dto;

public record ZipCodeRequestDto(
    String zip,
    String city,
    String county,
    String state,
    String timezone,
    String type
) {
}