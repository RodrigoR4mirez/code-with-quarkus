package org.acme.dto;

import java.util.List;

/**
 * Pagina de codigos postales con total de registros.
 */
public record ZipCodePage(
    List<ZipCodeResponseDto> items,
    long total
) {
}
