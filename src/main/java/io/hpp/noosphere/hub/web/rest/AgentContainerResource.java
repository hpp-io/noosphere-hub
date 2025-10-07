package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.repository.AgentContainerRepository;
import io.hpp.noosphere.hub.service.AgentContainerService;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentContainerVm;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.AgentContainer}.
 */
@RestController
@RequestMapping("/api/agent-containers")
public class AgentContainerResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentContainerResource.class);

    private static final String ENTITY_NAME = "nooSphereHubAgentContainer";
    private final AgentContainerService agentContainerService;
    private final AgentContainerRepository agentContainerRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AgentContainerResource(AgentContainerService agentContainerService, AgentContainerRepository agentContainerRepository) {
        this.agentContainerService = agentContainerService;
        this.agentContainerRepository = agentContainerRepository;
    }

    /**
     * {@code POST  /agent-containers} : Create a new agentContainer.
     *
     * @param agentContainerDTO the agentContainerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentContainerDTO, or with status {@code 400 (Bad Request)} if
     * the agentContainer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AgentContainerDTO> createAgentContainer(@Valid @RequestBody AgentContainerDTO agentContainerDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AgentContainer : {}", agentContainerDTO);
        if (agentContainerDTO.getId() != null) {
            throw new BadRequestAlertException("A new agentContainer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Instant now = Instant.now();
        agentContainerDTO = agentContainerService.save(agentContainerDTO, now);
        return ResponseEntity.created(new URI("/api/agent-containers/" + agentContainerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentContainerDTO.getId().toString()))
            .body(agentContainerDTO);
    }

    /**
     * {@code PUT  /agent-containers/:id} : Updates an existing agentContainer.
     *
     * @param id                the id of the agentContainerDTO to save.
     * @param agentContainerDTO the agentContainerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentContainerDTO, or with status {@code 400 (Bad Request)} if
     * the agentContainerDTO is not valid, or with status {@code 500 (Internal Server Error)} if the agentContainerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AgentContainerDTO> updateAgentContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody AgentContainerDTO agentContainerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AgentContainer : {}, {}", id, agentContainerDTO);
        if (agentContainerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentContainerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentContainerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agentContainerDTO = agentContainerService.update(agentContainerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentContainerDTO.getId().toString()))
            .body(agentContainerDTO);
    }

    /**
     * {@code PATCH  /agent-containers/:id} : Partial updates given fields of an existing agentContainer, field will ignore if it is null
     *
     * @param id                the id of the agentContainerDTO to save.
     * @param agentContainerDTO the agentContainerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentContainerDTO, or with status {@code 400 (Bad Request)} if
     * the agentContainerDTO is not valid, or with status {@code 404 (Not Found)} if the agentContainerDTO is not found, or with status
     * {@code 500 (Internal Server Error)} if the agentContainerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AgentContainerDTO> partialUpdateAgentContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody AgentContainerDTO agentContainerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AgentContainer partially : {}, {}", id, agentContainerDTO);
        if (agentContainerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentContainerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentContainerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgentContainerDTO> result = agentContainerService.partialUpdate(agentContainerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentContainerDTO.getId().toString())
        );
    }

    /**
     * {@code POST  /agent-containers/search} : search agentContainers.
     *
     * @param searchVm the search criteria of the request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of agentContainers in body.
     */
    @PostMapping("/search")
    public ResponseEntity<List<AgentContainerDTO>> search(
        @RequestBody SearchAgentContainerVm searchVm,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of AgentContainers");
        Page<AgentContainerDTO> page = agentContainerService.search(
            searchVm.getAgentName(),
            searchVm.getContainerName(),
            searchVm.getStatusCode(),
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agent-containers/:id} : get the "id" agentContainer.
     *
     * @param id the id of the agentContainerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agentContainerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AgentContainerDTO> getAgentContainer(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get AgentContainer : {}", id);
        Optional<AgentContainerDTO> agentContainerDTO = agentContainerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agentContainerDTO);
    }

    /**
     * {@code DELETE  /agent-containers/:id} : delete the "id" agentContainer.
     *
     * @param id the id of the agentContainerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgentContainer(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete AgentContainer : {}", id);
        agentContainerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
