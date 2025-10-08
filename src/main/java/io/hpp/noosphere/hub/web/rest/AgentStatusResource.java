package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.exception.AgentNotFoundException;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.repository.AgentStatusRepository;
import io.hpp.noosphere.hub.service.AgentStatusService;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.AgentStatus}.
 */
@RestController
@RequestMapping("/api/agents")
public class AgentStatusResource {

  private static final Logger LOG = LoggerFactory.getLogger(AgentStatusResource.class);

  private static final String ENTITY_NAME = "agentStatus";
  private final AgentStatusService agentStatusService;
  private final AgentStatusRepository agentStatusRepository;
  private final IAuthenticationFacade authenticationFacade;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public AgentStatusResource(AgentStatusService agentStatusService,
    AgentStatusRepository agentStatusRepository,
    AgentRepository agentRepository,
    IAuthenticationFacade authenticationFacade) {
    this.agentStatusService = agentStatusService;
    this.agentStatusRepository = agentStatusRepository;
    this.authenticationFacade = authenticationFacade;
  }


  @Tag(name = "Agent", description = "Agent Controller")
  @Operation(
    summary = "Keep Alive Agent"
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE, array = @ArraySchema(schema = @Schema(implementation = AgentDTO.class))), description = "Successful operation"),
    @ApiResponse(responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE), description = "Internal server error")
  })
  @PutMapping("/{agentId}/keep-alive")
  public ResponseEntity<AgentStatusDTO> keepAlive(
    @Parameter(description = "Agent ID", required = true)
    @PathVariable(value = "agentId", required = true) final UUID agentId
  ) throws AgentNotFoundException, PermissionDeniedException {
    LOG.debug("REST request to keep alive agent : {}", agentId);

    Instant now = Instant.now();
    AgentStatusDTO agentStatusDTO = agentStatusService.updateKeepAlive(authenticationFacade.getUserId(), agentId, now);
    return ResponseEntity.ok().body(agentStatusDTO);
  }
}
