package org.acme.service.impl;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import org.acme.dto.ZipCodePage;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.dto.ZipCodeResponseDto;
import org.acme.entity.ZipCodeEntity;
import org.acme.repository.ZipCodeRepository;
import org.acme.service.ZipCodeService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZipCodeServiceImpl implements ZipCodeService {

  @Inject
  ZipCodeRepository zipCodeRepository;

  @Override
  /**
   * Recupera un código postal por su ID.
   *
   * @param zip identificador del código postal
   * @return ZipCodeResponseDto si existe, error si no
   */
  public Uni<ZipCodeResponseDto> findById(String zip) {
    return zipCodeRepository
        .findById(zip)
        .onItem().ifNotNull().transform(this::entityToResponse)
        .onItem().ifNull().failWith(() -> new RuntimeException("ZipCode not found: " + zip));
  }

  @Override
  /**
   * Obtiene los primeros N códigos postales y el total disponible.
   *
   * @return ZipCodePage con items y total
   */
  @WithSession
  public Uni<ZipCodePage> findFirstWithTotal() {
    var pageUni = fetchFirstPage();
    var countUni = zipCodeRepository.count();
    return Uni.combine().all().unis(pageUni, countUni)
        .asTuple()
        .map(tuple -> new ZipCodePage(tuple.getItem1(), tuple.getItem2()));
  }

  /**
   * Metodo que crea un Zip si no existe, pero si existe lo obtiene.
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

  @Override
  /**
   * Elimina todos los códigos postales de la base de datos.
   *
   * @return cantidad de registros eliminados
   */
  @WithTransaction
  public Uni<Long> deleteAll() {
    return zipCodeRepository.deleteAll();
  }

  @Override
  @WithSession
  public Uni<Long> countAll() {
    return zipCodeRepository.count();
  }

  private Uni<List<ZipCodeResponseDto>> fetchFirstPage() {
    return zipCodeRepository.findAll()
        .list()
        .onItem().transform(this::mapEntitiesToResponses);
  }

  private List<ZipCodeResponseDto> mapEntitiesToResponses(List<ZipCodeEntity> entities) {
    return entities
        .stream()
        .map(this::entityToResponse)
        .collect(Collectors.toList());
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
