package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.AgentContainer;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentContainerRepository;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.mapper.AgentContainerMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.AgentContainer}.
 */
@Service
@Transactional
public class AgentContainerService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentContainerService.class);

    private final AgentContainerRepository agentContainerRepository;

    private final AgentContainerMapper agentContainerMapper;

    public AgentContainerService(AgentContainerRepository agentContainerRepository, AgentContainerMapper agentContainerMapper) {
        this.agentContainerRepository = agentContainerRepository;
        this.agentContainerMapper = agentContainerMapper;
    }

    public AgentContainerDTO create(AgentService agentService, String userId, UUID agentId, UUID containerId, Instant timestamp)
        throws PermissionDeniedException {
        LOG.debug("Request to save Agent {}, Container {}", agentId, containerId);
        agentService.validateOwner(agentId, userId);
        AgentContainerDTO agentContainerDTO = new AgentContainerDTO();
        AgentDTO agentDTO = new AgentDTO();
        agentDTO.setId(agentId);
        agentContainerDTO.setAgent(agentDTO);
        ContainerDTO containerDTO = new ContainerDTO();
        containerDTO.setId(containerId);
        agentContainerDTO.setContainer(containerDTO);
        agentContainerDTO.setCreatedAt(timestamp);
        agentContainerDTO.setStatusCode(StatusCode.ACTIVE);
        AgentContainer agentContainer = agentContainerMapper.toEntity(agentContainerDTO);
        agentContainer = agentContainerRepository.save(agentContainer);
        return agentContainerMapper.toDto(agentContainer);
    }

    /**
     * Get all the agentContainers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AgentContainerDTO> search(UUID agentId, String containerName, StatusCode statusCode, Pageable pageable) {
        LOG.debug("Request to search AgentContainers");
        return agentContainerRepository.search(agentId, containerName, statusCode, pageable).map(agentContainerMapper::toDto);
    }

    /**
     * Get one agentContainer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AgentContainerDTO> findOne(UUID id) {
        LOG.debug("Request to get AgentContainer : {}", id);
        return agentContainerRepository.findById(id).map(agentContainerMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AgentContainerDTO> findOne(UUID agentId, UUID containerId) {
        LOG.debug("Request to get Agent {}, Container {}", agentId, containerId);
        return agentContainerRepository.findByAgentIdAndContainerId(agentId, containerId).map(agentContainerMapper::toDto);
    }

    public void delete(AgentService agentService, String userId, UUID agentId, UUID id) throws PermissionDeniedException {
        LOG.debug("Request to delete AgentContainer : {}", id);
        agentService.validateOwner(agentId, userId);
        Optional<AgentContainer> optionalAgentContainer = agentContainerRepository.findByAgentIdAndContainerId(agentId, id);
        optionalAgentContainer.ifPresent(agentContainerRepository::delete);
    }
}
