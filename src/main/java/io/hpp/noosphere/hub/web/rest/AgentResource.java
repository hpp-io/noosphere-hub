package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.service.AgentService;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.Agent}.
 */
@RestController
@RequestMapping("/api/agents")
public class AgentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentResource.class);

    private static final String ENTITY_NAME = "nooSphereHubAgent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IAuthenticationFacade authenticationFacade;
    private final AgentService agentService;

    private final AgentRepository agentRepository;

    public AgentResource(AgentService agentService, AgentRepository agentRepository,
      IAuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
        this.agentService = agentService;
        this.agentRepository = agentRepository;
    }

    /**
     * {@code POST  /agents} : Create a new agent.
     *
     * @param agentDTO the agentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentDTO, or with status {@code 400 (Bad Request)} if the agent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AgentDTO> createAgent(@Valid @RequestBody AgentDTO agentDTO) throws URISyntaxException {
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
     * @param id the id of the agentDTO to save.
     * @param agentDTO the agentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO,
     * or with status {@code 400 (Bad Request)} if the agentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
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
     * {@code GET  /agents} : get all the agents.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AgentDTO>> getAllAgents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("agentstatus-is-null".equals(filter)) {
            LOG.debug("REST request to get all Agents where agentStatus is null");
            return new ResponseEntity<>(agentService.findAllWhereAgentStatusIsNull(), HttpStatus.OK);
        }
        LOG.debug("REST request to get a page of Agents");
        Page<AgentDTO> page = agentService.findAll(pageable);
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
