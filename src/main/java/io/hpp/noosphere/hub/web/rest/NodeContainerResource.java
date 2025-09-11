package io.hpp.noosphere.hub.web.rest;

import io.hpp.noosphere.hub.repository.NodeContainerRepository;
import io.hpp.noosphere.hub.service.NodeContainerService;
import io.hpp.noosphere.hub.service.dto.NodeContainerDTO;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.NodeContainer}.
 */
@RestController
@RequestMapping("/api/node-containers")
public class NodeContainerResource {

    private static final Logger LOG = LoggerFactory.getLogger(NodeContainerResource.class);

    private static final String ENTITY_NAME = "noosphereHubNodeContainer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NodeContainerService nodeContainerService;

    private final NodeContainerRepository nodeContainerRepository;

    public NodeContainerResource(NodeContainerService nodeContainerService, NodeContainerRepository nodeContainerRepository) {
        this.nodeContainerService = nodeContainerService;
        this.nodeContainerRepository = nodeContainerRepository;
    }

    /**
     * {@code POST  /node-containers} : Create a new nodeContainer.
     *
     * @param nodeContainerDTO the nodeContainerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nodeContainerDTO, or with status {@code 400 (Bad Request)} if the nodeContainer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NodeContainerDTO> createNodeContainer(@Valid @RequestBody NodeContainerDTO nodeContainerDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save NodeContainer : {}", nodeContainerDTO);
        if (nodeContainerDTO.getId() != null) {
            throw new BadRequestAlertException("A new nodeContainer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        nodeContainerDTO = nodeContainerService.save(nodeContainerDTO);
        return ResponseEntity.created(new URI("/api/node-containers/" + nodeContainerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, nodeContainerDTO.getId().toString()))
            .body(nodeContainerDTO);
    }

    /**
     * {@code PUT  /node-containers/:id} : Updates an existing nodeContainer.
     *
     * @param id the id of the nodeContainerDTO to save.
     * @param nodeContainerDTO the nodeContainerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeContainerDTO,
     * or with status {@code 400 (Bad Request)} if the nodeContainerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the nodeContainerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NodeContainerDTO> updateNodeContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @Valid @RequestBody NodeContainerDTO nodeContainerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NodeContainer : {}, {}", id, nodeContainerDTO);
        if (nodeContainerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nodeContainerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nodeContainerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        nodeContainerDTO = nodeContainerService.update(nodeContainerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nodeContainerDTO.getId().toString()))
            .body(nodeContainerDTO);
    }

    /**
     * {@code PATCH  /node-containers/:id} : Partial updates given fields of an existing nodeContainer, field will ignore if it is null
     *
     * @param id the id of the nodeContainerDTO to save.
     * @param nodeContainerDTO the nodeContainerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nodeContainerDTO,
     * or with status {@code 400 (Bad Request)} if the nodeContainerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the nodeContainerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the nodeContainerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NodeContainerDTO> partialUpdateNodeContainer(
        @PathVariable(value = "id", required = false) final UUID id,
        @NotNull @RequestBody NodeContainerDTO nodeContainerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NodeContainer partially : {}, {}", id, nodeContainerDTO);
        if (nodeContainerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, nodeContainerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!nodeContainerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NodeContainerDTO> result = nodeContainerService.partialUpdate(nodeContainerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, nodeContainerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /node-containers} : get all the nodeContainers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nodeContainers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NodeContainerDTO>> getAllNodeContainers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of NodeContainers");
        Page<NodeContainerDTO> page = nodeContainerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /node-containers/:id} : get the "id" nodeContainer.
     *
     * @param id the id of the nodeContainerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the nodeContainerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NodeContainerDTO> getNodeContainer(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get NodeContainer : {}", id);
        Optional<NodeContainerDTO> nodeContainerDTO = nodeContainerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(nodeContainerDTO);
    }

    /**
     * {@code DELETE  /node-containers/:id} : delete the "id" nodeContainer.
     *
     * @param id the id of the nodeContainerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNodeContainer(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete NodeContainer : {}", id);
        nodeContainerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
