package org.acme.service;

import io.smallrye.mutiny.Uni;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.dto.ZipCodeResponseDto;

import java.util.Map;

public interface ZipCodeService {

  Uni<Map.Entry<ZipCodeResponseDto, Boolean>> create(ZipCodeRequestDto zipCodeRequestDto);
}