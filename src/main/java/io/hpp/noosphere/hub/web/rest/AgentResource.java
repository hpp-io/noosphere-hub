package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.service.AgentService;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.Agent}.
 */
@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agent", description = "Agent Controller")
public class AgentResource {

  private static final Logger LOG = LoggerFactory.getLogger(AgentResource.class);

  private static final String ENTITY_NAME = "nooSphereHubAgent";
  private final IAuthenticationFacade authenticationFacade;
  private final AgentService agentService;
  private final AgentRepository agentRepository;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public AgentResource(AgentService agentService, AgentRepository agentRepository, IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
    this.agentService = agentService;
    this.agentRepository = agentRepository;
  }

  /**
   * {@code POST  /agents} : Create a new agent.
   *
   * @param agentDTO the agentDTO to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentDTO, or with status {@code 400 (Bad Request)} if the agent
   * has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("")
  public ResponseEntity<AgentDTO> createAgent(@RequestBody AgentDTO agentDTO) throws URISyntaxException {
    LOG.debug("REST request to save Agent : {}", agentDTO);
    if (agentDTO.getId() != null) {
      throw new BadRequestAlertException("A new agent cannot already have an ID", ENTITY_NAME, "idexists");
    }
    Instant now = Instant.now();
    agentDTO = agentService.save(authenticationFacade.getUserId(), agentDTO, now);
    return ResponseEntity.created(new URI("/api/agents/" + agentDTO.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString()))
      .body(agentDTO);
  }

  /**
   * {@code PUT  /agents/:id} : Updates an existing agent.
   *
   * @param id       the id of the agentDTO to save.
   * @param agentDTO the agentDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO, or with status {@code 400 (Bad Request)} if the
   * agentDTO is not valid, or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  public ResponseEntity<AgentDTO> updateAgent(
    @PathVariable(value = "id", required = false) final UUID id,
    @Valid @RequestBody AgentDTO agentDTO
  ) throws URISyntaxException {
    LOG.debug("REST request to update Agent : {}, {}", id, agentDTO);
    if (agentDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, agentDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!agentRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }

    agentDTO = agentService.update(agentDTO);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString()))
      .body(agentDTO);
  }


  /**
   * {@code POST  /agents/search} : search agents.
   *
   * @param searchVm the search criteria of the request.
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agents in body.
   */
  @PostMapping("/search")
  @Operation(
    summary = "Search Agent",
    description = "Search Agent Method"
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Search Criteria", content = @Content(schema = @Schema(implementation = SearchAgentVm.class), mediaType = "application/json"))
  @ApiResponses({
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE, array = @ArraySchema(schema = @Schema(implementation = AgentDTO.class))), description = "Successful operation"),
    @ApiResponse(responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE), description = "Internal server error")
  })
  @JsonView(JsonViewType.Shallow.class)
  public ResponseEntity<List<AgentDTO>> search(
    @RequestBody SearchAgentVm searchVm,
    @org.springdoc.core.annotations.ParameterObject Pageable pageable
  ) {
    LOG.debug("REST request to search Agents");
    Page<AgentDTO> page = agentService.search(searchVm.getName(), searchVm.getStatusCode(), searchVm.getCreatedByUserId(), pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /agents/:id} : get the "id" agent.
   *
   * @param id the id of the agentDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agentDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  public ResponseEntity<AgentDTO> getAgent(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get Agent : {}", id);
    Optional<AgentDTO> agentDTO = agentService.findOne(id);
    return ResponseUtil.wrapOrNotFound(agentDTO);
  }

  /**
   * {@code DELETE  /agents/:id} : delete the "id" agent.
   *
   * @param id the id of the agentDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAgent(@PathVariable("id") UUID id) {
    LOG.debug("REST request to delete Agent : {}", id);
    agentService.delete(id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }
}
