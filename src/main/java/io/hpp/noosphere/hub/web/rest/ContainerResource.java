package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.ContainerRepository;
import io.hpp.noosphere.hub.service.ContainerService;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentVm;
import io.hpp.noosphere.hub.web.rest.vm.SearchContainerVm;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.Container}.
 */
@RestController
@RequestMapping("/api/containers")
@Tag(name = "Container", description = "Container Controller")
public class ContainerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerResource.class);

    private static final String ENTITY_NAME = "nooSphereHubContainer";
    private final IAuthenticationFacade authenticationFacade;
    private final ContainerService containerService;
    private final ContainerRepository containerRepository;

    private final UserService userService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ContainerResource(
        ContainerService containerService,
        ContainerRepository containerRepository,
        UserService userService,
        IAuthenticationFacade authenticationFacade
    ) {
        this.containerService = containerService;
        this.containerRepository = containerRepository;
        this.authenticationFacade = authenticationFacade;
        this.userService = userService;
    }

    /**
     * {@code POST  /containers} : Create a new container.
     *
     * @param containerDTO the containerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new containerDTO, or with status {@code 400 (Bad Request)} if the
     * container has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @JsonView(JsonViewType.Update.class)
    public ResponseEntity<ContainerDTO> createContainer(@RequestBody ContainerDTO containerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Container : {}", containerDTO);
        if (containerDTO.getId() != null) {
            throw new BadRequestAlertException("A new container cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Instant now = Instant.now();
        containerDTO = containerService.create(authenticationFacade.getUserId(), containerDTO, now);
        return ResponseEntity.created(new URI("/api/containers/" + containerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, containerDTO.getId().toString()))
            .body(containerDTO);
    }

    /**
     * {@code PUT  /containers/:id} : Updates an existing container.
     *
     * @param id           the id of the containerDTO to save.
     * @param containerDTO the containerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated containerDTO, or with status {@code 400 (Bad Request)} if the
     * containerDTO is not valid, or with status {@code 500 (Internal Server Error)} if the containerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @JsonView(JsonViewType.Update.class)
    public ResponseEntity<ContainerDTO> updateContainer(
        @PathVariable(value = "id", required = true) final UUID id,
        @Valid @RequestBody ContainerDTO containerDTO
    ) throws PermissionDeniedException {
        LOG.debug("REST request to update Container : {}, {}", id, containerDTO);
        if (containerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, containerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!containerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Instant now = Instant.now();
        containerDTO = containerService.partialUpdate(userService, authenticationFacade.getUserId(), containerDTO, now);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, containerDTO.getId().toString()))
            .body(containerDTO);
    }

    /**
     * {@code POST  /containers/search} : search containers.
     *
     * @param searchVm the search criteria of the request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of containers in body.
     */
    @PostMapping("/search")
    @Operation(summary = "Search Container")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        description = "Search Criteria",
        content = @Content(schema = @Schema(implementation = SearchContainerVm.class), mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE)
    )
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = ContainerDTO.class))
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
    public ResponseEntity<List<ContainerDTO>> search(
        @RequestBody SearchContainerVm searchVm,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search Containers");
        Page<ContainerDTO> page = containerService.search(
            searchVm.getName(),
            searchVm.getStatusCode(),
            searchVm.getCreatedByUserId(),
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /containers/:id} : get the "id" container.
     *
     * @param id the id of the containerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the containerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @JsonView(JsonViewType.Update.class)
    public ResponseEntity<ContainerDTO> getContainer(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get Container : {}", id);
        Optional<ContainerDTO> containerDTO = containerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(containerDTO);
    }

    /**
     * {@code DELETE  /containers/:id} : delete the "id" container.
     *
     * @param id the id of the containerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContainer(@PathVariable("id") UUID id) throws PermissionDeniedException {
        LOG.debug("REST request to delete Container : {}", id);
        containerService.delete(authenticationFacade.getUserId(), id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
