package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.repository.NodeStatusRepository;
import io.hpp.noosphere.hub.service.NodeStatusService;
import io.hpp.noosphere.hub.service.dto.NodeStatusDTO;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.NodeStatus}.
 */
@RestController
@RequestMapping("/api/node-statuses")
public class NodeStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(NodeStatusResource.class);

    private static final String ENTITY_NAME = "noosphereHubNodeStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NodeStatusService nodeStatusService;

    private final NodeStatusRepository nodeStatusRepository;

    public NodeStatusResource(NodeStatusService nodeStatusService, NodeStatusRepository nodeStatusRepository) {
        this.nodeStatusService = nodeStatusService;
        this.nodeStatusRepository = nodeStatusRepository;
    }

    /**
     * {@code POST  /node-statuses} : Create a new nodeStatus.
     *
     * @param nodeStatusDTO the nodeStatusDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nodeStatusDTO, or with status {@code 400 (Bad Request)} if the nodeStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NodeStatusDTO> createNodeStatus(@Valid @RequestBody NodeStatusDTO nodeStatusDTO) throws URISyntaxException {
        LOG.debug("REST request to save NodeStatus : {}", nodeStatusDTO);
        if (nodeStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new nodeStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        nodeStatusDTO = nodeStatusService.save(nodeStatusDTO);
        return ResponseEntity.created(new URI("/api/node-statuses/" + nodeStatusDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, nodeStatusDTO.getId().toString()))
            .body(nodeStatusDTO);
    }

    /**
     * {@code PUT  /node-statuses/:id} : Updates an existing nodeStatus.
     *
     * @param id the id of the nodeStatusDTO to save.
     * @param nodeStatusDTO the nodeStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeStatusDTO,
     * or with status {@code 400 (Bad Request)} if the nodeStatusDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the nodeStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NodeStatusDTO> updateNodeStatus(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody NodeStatusDTO nodeStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NodeStatus : {}, {}", id, nodeStatusDTO);
        if (nodeStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nodeStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nodeStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        nodeStatusDTO = nodeStatusService.update(nodeStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nodeStatusDTO.getId().toString()))
            .body(nodeStatusDTO);
    }

    /**
     * {@code PATCH  /node-statuses/:id} : Partial updates given fields of an existing nodeStatus, field will ignore if it is null
     *
     * @param id the id of the nodeStatusDTO to save.
     * @param nodeStatusDTO the nodeStatusDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeStatusDTO,
     * or with status {@code 400 (Bad Request)} if the nodeStatusDTO is not valid,
     * or with status {@code 404 (Not Found)} if the nodeStatusDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the nodeStatusDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NodeStatusDTO> partialUpdateNodeStatus(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody NodeStatusDTO nodeStatusDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NodeStatus partially : {}, {}", id, nodeStatusDTO);
        if (nodeStatusDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nodeStatusDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nodeStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NodeStatusDTO> result = nodeStatusService.partialUpdate(nodeStatusDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nodeStatusDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /node-statuses} : get all the nodeStatuses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nodeStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NodeStatusDTO>> getAllNodeStatuses(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of NodeStatuses");
        Page<NodeStatusDTO> page = nodeStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /node-statuses/:id} : get the "id" nodeStatus.
     *
     * @param id the id of the nodeStatusDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the nodeStatusDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NodeStatusDTO> getNodeStatus(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get NodeStatus : {}", id);
        Optional<NodeStatusDTO> nodeStatusDTO = nodeStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(nodeStatusDTO);
    }

    /**
     * {@code DELETE  /node-statuses/:id} : delete the "id" nodeStatus.
     *
     * @param id the id of the nodeStatusDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNodeStatus(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete NodeStatus : {}", id);
        nodeStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
