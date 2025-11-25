package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.common.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.exception.AgentNotFoundException;
import io.hpp.noosphere.hub.exception.AgentRequestNotFoundException;
import io.hpp.noosphere.hub.repository.AgentRequestRepository;
import io.hpp.noosphere.hub.service.AgentRequestService;
import io.hpp.noosphere.hub.service.AgentService;
import io.hpp.noosphere.hub.service.dto.AgentRequestDTO;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentRequestVm;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.AgentRequest}.
 */
@RestController
@RequestMapping("/api/agent-requests")
@Tag(
  name = "Agent Request",
  description = "Agent Request Controller",
  extensions = { @Extension(properties = { @ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "3") }) }
)
public class AgentRequestResource {

  private static final Logger LOG = LoggerFactory.getLogger(AgentRequestResource.class);

  private static final String ENTITY_NAME = "agentRequest";
  private final AgentRequestService agentRequestService;
  private final AgentRequestRepository agentRequestRepository;
  private final AgentService agentService;
  private final IAuthenticationFacade authenticationFacade;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public AgentRequestResource(
    AgentRequestService agentRequestService,
    AgentService agentService,
    AgentRequestRepository agentRequestRepository,
    IAuthenticationFacade authenticationFacade
  ) {
    this.agentRequestService = agentRequestService;
    this.agentRequestRepository = agentRequestRepository;
    this.authenticationFacade = authenticationFacade;
    this.agentService = agentService;
  }

  @PutMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<AgentRequestDTO> updateAgentRequest(
    @PathVariable(value = "id", required = true) final UUID id,
    @Valid @RequestBody AgentRequestDTO agentRequestDTO
  ) throws PermissionDeniedException, AgentNotFoundException, AgentRequestNotFoundException {
    LOG.debug("REST request to update AgentRequest : {}, {}", id, agentRequestDTO);
    if (agentRequestDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, agentRequestDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!agentRequestRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }
    Instant now = Instant.now();
    agentRequestDTO = agentRequestService.update(agentService, authenticationFacade.getUserId(), id, agentRequestDTO.getStatusCode(), now);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentRequestDTO.getId().toString()))
      .body(agentRequestDTO);
  }

  @PostMapping("/search")
  @Operation(summary = "Search Agent Requests")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    required = true,
    description = "Search Criteria",
    content = @Content(schema = @Schema(implementation = SearchAgentVm.class), mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE)
  )
  @ApiResponses(
    {
      @ApiResponse(
        responseCode = "200",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = AgentRequestDTO.class))
        ),
        description = "Successful operation"
      ),
      @ApiResponse(
        responseCode = "500",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE),
        description = "Internal server error"
      ),
    }
  )
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<List<AgentRequestDTO>> search(
    @RequestBody SearchAgentRequestVm searchAgentRequestVm,
    @org.springdoc.core.annotations.ParameterObject Pageable pageable
  ) {
    LOG.debug("REST request to search Agents");
    Page<AgentRequestDTO> page = agentRequestService.search(
      searchAgentRequestVm.getContainerId(),
      searchAgentRequestVm.getAgentId(),
      searchAgentRequestVm.getAgentName(),
      searchAgentRequestVm.getStatusCode(),
      pageable
    );
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  @GetMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<AgentRequestDTO> getAgentRequest(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get AgentRequest : {}", id);
    Optional<AgentRequestDTO> agentRequestDTO = agentRequestService.findOne(id);
    return ResponseUtil.wrapOrNotFound(agentRequestDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAgentRequest(@PathVariable("id") UUID id)
    throws PermissionDeniedException, AgentNotFoundException, AgentRequestNotFoundException {
    LOG.debug("REST request to delete AgentRequest : {}", id);
    agentRequestService.delete(agentService, authenticationFacade.getUserId(), id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }
}
