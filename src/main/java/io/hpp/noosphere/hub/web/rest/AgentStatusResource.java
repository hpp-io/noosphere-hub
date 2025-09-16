package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.repository.AgentStatusRepository;
import io.hpp.noosphere.hub.service.AgentStatusService;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.AgentStatus}.
 */
@RestController
@RequestMapping("/api/agent-statuses")
public class AgentStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentStatusResource.class);

    private static final String ENTITY_NAME = "nooSphereHubAgentStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AgentStatusService agentStatusService;

    private final AgentStatusRepository agentStatusRepository;

    public AgentStatusResource(AgentStatusService agentStatusService, AgentStatusRepository agentStatusRepository) {
        this.agentStatusService = agentStatusService;
        this.agentStatusRepository = agentStatusRepository;
    }

    /**
     * {@code POST  /agent-statuses} : Create a new agentStatus.
     *
     * @param agentStatusDTO the agentStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentStatusDTO, or with status {@code 400 (Bad Request)} if the agentStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AgentStatusDTO> createAgentStatus(@Valid @RequestBody AgentStatusDTO agentStatusDTO) throws URISyntaxException {
        LOG.debug("REST request to save AgentStatus : {}", agentStatusDTO);
        if (agentStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new agentStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        agentStatusDTO = agentStatusService.save(agentStatusDTO);
        return ResponseEntity.created(new URI("/api/agent-statuses/" + agentStatusDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentStatusDTO.getId().toString()))
            .body(agentStatusDTO);
    }

    /**
     * {@code PUT  /agent-statuses/:id} : Updates an existing agentStatus.
     *
     * @param id the id of the agentStatusDTO to save.
     * @param agentStatusDTO the agentStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentStatusDTO,
     * or with status {@code 400 (Bad Request)} if the agentStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agentStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AgentStatusDTO> updateAgentStatus(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody AgentStatusDTO agentStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AgentStatus : {}, {}", id, agentStatusDTO);
        if (agentStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agentStatusDTO = agentStatusService.update(agentStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentStatusDTO.getId().toString()))
            .body(agentStatusDTO);
    }

    /**
     * {@code PATCH  /agent-statuses/:id} : Partial updates given fields of an existing agentStatus, field will ignore if it is null
     *
     * @param id the id of the agentStatusDTO to save.
     * @param agentStatusDTO the agentStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentStatusDTO,
     * or with status {@code 400 (Bad Request)} if the agentStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the agentStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the agentStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AgentStatusDTO> partialUpdateAgentStatus(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody AgentStatusDTO agentStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AgentStatus partially : {}, {}", id, agentStatusDTO);
        if (agentStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgentStatusDTO> result = agentStatusService.partialUpdate(agentStatusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentStatusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /agent-statuses} : get all the agentStatuses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agentStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AgentStatusDTO>> getAllAgentStatuses(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of AgentStatuses");
        Page<AgentStatusDTO> page = agentStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agent-statuses/:id} : get the "id" agentStatus.
     *
     * @param id the id of the agentStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agentStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgentStatusDTO> getAgentStatus(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get AgentStatus : {}", id);
        Optional<AgentStatusDTO> agentStatusDTO = agentStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agentStatusDTO);
    }

    /**
     * {@code DELETE  /agent-statuses/:id} : delete the "id" agentStatus.
     *
     * @param id the id of the agentStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgentStatus(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete AgentStatus : {}", id);
        agentStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
