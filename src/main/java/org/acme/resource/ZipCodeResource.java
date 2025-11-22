package org.acme.resource;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ZipCodeRequestDto;
import org.acme.service.ZipCodeService;
import org.jboss.logging.Logger;

@Path("/v1/zip-code")
public class ZipCodeResource {

  private static final Logger LOG = Logger.getLogger(ZipCodeResource.class);

  @Inject
  ZipCodeService zipCodeService;

  @POST
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
}