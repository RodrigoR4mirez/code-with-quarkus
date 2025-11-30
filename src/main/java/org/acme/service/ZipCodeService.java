package org.acme.service;

import io.smallrye.mutiny.Uni;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.dto.ZipCodeResponseDto;
import org.acme.dto.ZipCodePage;

import java.util.List;
import java.util.Map;

public interface ZipCodeService {

  /**
   * Busca un código postal por su ID.
   *
   * @param zip Código postal a buscar
   * @return Uni con el ZipCodeResponseDto si existe
   * @throws RuntimeException si el código postal no existe
   */
  Uni<ZipCodeResponseDto> findById(String zip);

  /**
   * Obtiene los primeros N códigos postales y el total disponible (paginado simple).
   *
   * @return Uni con los registros obtenidos y el total
   */
  Uni<ZipCodePage> findFirstWithTotal();

  /**
   * Cuenta el total de códigos postales almacenados.
   *
   * @return Uni con el total de registros
   */
  Uni<Long> countAll();

  /**
   * Crea un código postal si no existe, o devuelve el existente si ya está registrado.
   * Este método es idempotente y maneja condiciones de carrera.
   *
   * @param zipCodeRequestDto Datos del código postal a crear
   * @return Uni con un Map.Entry donde la clave es el ZipCodeResponseDto y el valor es
   *         true si fue creado, false si ya existía
   */
  Uni<Map.Entry<ZipCodeResponseDto, Boolean>> create(ZipCodeRequestDto zipCodeRequestDto);

  /**
   * Elimina todos los códigos postales de la base de datos.
   * ADVERTENCIA: Esta operación es destructiva e irreversible.
   *
   * @return Uni con el número de registros eliminados
   */
  Uni<Long> deleteAll();
}
