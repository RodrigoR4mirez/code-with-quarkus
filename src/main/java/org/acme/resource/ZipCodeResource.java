package org.acme.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.service.ZipCodeService;
import org.jboss.logging.Logger;

@Path("/v1/zip-code")
public class ZipCodeResource {

  private static final Logger LOG = Logger.getLogger(ZipCodeResource.class);

  @Inject
  ZipCodeService zipCodeService;

  /**
   * Lista los primeros N códigos postales junto con el total de registros.
   *
   * @return respuesta HTTP con los registros y el total
   */
  @GET
  @Path("/all")
  public Uni<Response> finAll() {
    return zipCodeService
        .findFirstWithTotal()
        .map(result -> Response.ok(result).build())
        .onFailure()
        .recoverWithItem(error -> {
          LOG.error("Error listing zip codes", error);
          return Response.serverError().entity(error.getMessage()).build();
        });
  }

  @GET
  @Path("/count")
  /**
   * Devuelve la cantidad total de registros de códigos postales.
   *
   * @return respuesta HTTP con el total de registros
   */
  public Uni<Response> count() {
    return zipCodeService
        .countAll()
        .map(total -> Response.ok(Map.of("total", total)).build())
        .onFailure()
        .recoverWithItem(error -> {
          LOG.error("Error counting zip codes", error);
          return Response.serverError().entity(error.getMessage()).build();
        });
  }

  /**
   * Busca un código postal por su identificador.
   *
   * @param zip código postal solicitado
   * @return respuesta HTTP con el registro o error 404
   */
  @GET
  @Path("/{zip}")
  public Uni<Response> findById(@PathParam("zip") String zip) {
    LOG.info("Finding zip code: " + zip);
    return zipCodeService
        .findById(zip)
        .map(zipCode ->
            Response.ok(zipCode).build()
        )
        .onFailure()
        .recoverWithItem(error -> {
          LOG.error("Error finding zip code: " + zip, error);
          return Response.status(Response.Status.NOT_FOUND)
              .entity("ZipCode not found: " + zip)
              .build();
        });
  }

  @POST
  /**
   * Crea un código postal o devuelve el existente si ya está registrado.
   *
   * @param request datos a persistir
   * @return respuesta HTTP con el recurso y estado 201/200
   */
  public Uni<Response> create(ZipCodeRequestDto request) {
    LOG.info("Creating zip code " + request.toString());
    return zipCodeService
        .create(request)
        .invoke(result -> {
          var message = result.getValue() ? "ZipCode creado" : "ZipCode ya existente";
          LOG.info(message);
        })
        .map(response ->
            Response
                .status(response.getValue() ? Response.Status.CREATED : Response.Status.OK)
                .entity(response.getKey())
                .build()
        )
        .onFailure(RuntimeException.class)
        .recoverWithItem(error ->
            Response.serverError().entity(error.getMessage()).build()
        );
  }

  @DELETE
  /**
   * Elimina todos los códigos postales.
   *
   * @return respuesta HTTP con la cantidad eliminada
   */
  public Uni<Response> deleteAll() {
    LOG.warn("Deleting all zip codes");
    return zipCodeService
        .deleteAll()
        .map(count -> Response.ok(count).build())
        .onFailure()
        .recoverWithItem(error -> {
          LOG.error("Error deleting zip codes", error);
          return Response.serverError().entity(error.getMessage()).build();
        });
  }


}
