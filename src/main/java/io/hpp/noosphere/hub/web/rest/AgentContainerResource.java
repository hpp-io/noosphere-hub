package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentContainerRepository;
import io.hpp.noosphere.hub.service.AgentContainerService;
import io.hpp.noosphere.hub.service.AgentService;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.web.rest.vm.SearchAgentContainerVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
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
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.AgentContainer}.
 */
@RestController
@RequestMapping("/api/agents")
@Tag(name = "Register Container", description = "Register Container Controller")
public class AgentContainerResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentContainerResource.class);

    private static final String ENTITY_NAME = "nooSphereHubAgentContainer";
    private final AgentContainerService agentContainerService;
    private final AgentService agentService;
    private final IAuthenticationFacade authenticationFacade;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AgentContainerResource(
        AgentContainerService agentContainerService,
        AgentService agentService,
        IAuthenticationFacade authenticationFacade
    ) {
        this.agentContainerService = agentContainerService;
        this.authenticationFacade = authenticationFacade;
        this.agentService = agentService;
    }

    @Operation(summary = "Register Container")
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    schema = @Schema(implementation = AgentContainerDTO.class),
                    mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE
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
    @JsonView(JsonViewType.Shallow.class)
    @PutMapping("/{agentId}/containers/{containerId}")
    public ResponseEntity<AgentContainerDTO> createAgentContainer(
        @PathVariable(value = "agentId", required = true) final UUID agentId,
        @PathVariable(value = "containerId", required = true) final UUID containerId
    ) throws URISyntaxException, PermissionDeniedException {
        LOG.debug("REST request to save Agent {}, Container {}", agentId, containerId);
        Instant now = Instant.now();
        AgentContainerDTO agentContainerDTO = agentContainerService.create(
            agentService,
            authenticationFacade.getUserId(),
            agentId,
            containerId,
            now
        );
        return ResponseEntity.created(new URI("/api/agent-containers/" + agentContainerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentContainerDTO.getId().toString()))
            .body(agentContainerDTO);
    }

    @Operation(summary = "Search Registered Containers")
    @PostMapping("/{agentId}/containers/search")
    @JsonView(JsonViewType.Shallow.class)
    public ResponseEntity<List<AgentContainerDTO>> search(
        @PathVariable(value = "agentId", required = true) final UUID agentId,
        @RequestBody SearchAgentContainerVm searchVm,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of AgentContainers");
        Page<AgentContainerDTO> page = agentContainerService.search(
            agentId,
            searchVm.getContainerName(),
            searchVm.getStatusCode(),
            pageable
        );
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @Operation(summary = "Get Registered Container")
    @GetMapping("/{agentId}/containers/{containerId}")
    @JsonView(JsonViewType.Shallow.class)
    public ResponseEntity<AgentContainerDTO> getAgentContainer(
        @PathVariable(value = "agentId", required = true) final UUID agentId,
        @PathVariable(value = "containerId", required = true) final UUID containerId
    ) {
        LOG.debug("REST request to get Agent {}, Container {}", agentId, containerId);
        Optional<AgentContainerDTO> agentContainerDTO = agentContainerService.findOne(agentId, containerId);
        return ResponseUtil.wrapOrNotFound(agentContainerDTO);
    }

    @Operation(summary = "Delete Registered Container")
    @DeleteMapping("/{agentId}/containers/{containerId}")
    public ResponseEntity<Void> deleteAgentContainer(
        @PathVariable(value = "agentId", required = true) final UUID agentId,
        @PathVariable(value = "containerId", required = true) final UUID containerId
    ) throws PermissionDeniedException {
        LOG.debug("REST request to delete AgentContainer : {}", containerId);
        agentContainerService.delete(agentService, authenticationFacade.getUserId(), agentId, containerId);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, containerId.toString()))
            .build();
    }
}
