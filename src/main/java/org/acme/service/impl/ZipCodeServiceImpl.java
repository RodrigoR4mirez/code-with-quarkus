package org.acme.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.dto.ZipCodeResponseDto;
import org.acme.entity.ZipCodeEntity;
import org.acme.repository.ZipCodeRepository;
import org.acme.service.ZipCodeService;
import java.util.Map;

@ApplicationScoped
public class ZipCodeServiceImpl implements ZipCodeService {

  @Inject
  ZipCodeRepository zipCodeRepository;

  /**
   * Metodo que crea un Zip si no existe, pero si existe la obtiene.
   *
   * @param zipCodeRequestDto Objeto a recibir
   * @return Objeto creado u obtenido
   */
  @Override
  @WithTransaction
  public Uni<Map.Entry<ZipCodeResponseDto, Boolean>> create(ZipCodeRequestDto zipCodeRequestDto) {
    return zipCodeRepository
        .findById(zipCodeRequestDto.zip())
        .onItem().ifNotNull().transform(existing ->
            Map.entry(entityToResponse(existing), Boolean.FALSE)
        )
        .onItem().ifNull().switchTo(() ->
            zipCodeRepository
                .persist(requestToEntity(zipCodeRequestDto))
                .replaceWith(Map.entry(requestToResponse(zipCodeRequestDto), Boolean.TRUE))
        )
        .onFailure(PersistenceException.class)
        .recoverWithUni(() ->
            zipCodeRepository.findById(zipCodeRequestDto.zip())
                .onItem().ifNotNull().transform(existing ->
                    Map.entry(entityToResponse(existing), Boolean.FALSE))
                .onItem().ifNull().switchTo(() ->
                    Uni.createFrom().failure(() -> new RuntimeException("ZipCode not found after failure")))
        );
  }

  private ZipCodeResponseDto entityToResponse(ZipCodeEntity entity) {
    return new ZipCodeResponseDto(
        entity.getZip(),
        entity.getCity(),
        entity.getCounty(),
        entity.getState(),
        entity.getTimezone(),
        entity.getType()
    );
  }

  private ZipCodeEntity requestToEntity(ZipCodeRequestDto request) {
    return new ZipCodeEntity(
        request.zip(),
        request.city(),
        request.county(),
        request.state(),
        request.timezone(),
        request.type()
    );
  }

  private ZipCodeResponseDto requestToResponse(ZipCodeRequestDto request) {
    return new ZipCodeResponseDto(
        request.zip(),
        request.city(),
        request.county(),
        request.state(),
        request.timezone(),
        request.type()
    );
  }
}