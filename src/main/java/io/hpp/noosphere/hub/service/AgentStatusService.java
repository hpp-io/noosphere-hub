package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.AgentStatus;
import io.hpp.noosphere.hub.repository.AgentStatusRepository;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import io.hpp.noosphere.hub.service.mapper.AgentStatusMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.AgentStatus}.
 */
@Service
@Transactional
public class AgentStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentStatusService.class);

    private final AgentStatusRepository agentStatusRepository;

    private final AgentStatusMapper agentStatusMapper;

    public AgentStatusService(AgentStatusRepository agentStatusRepository, AgentStatusMapper agentStatusMapper) {
        this.agentStatusRepository = agentStatusRepository;
        this.agentStatusMapper = agentStatusMapper;
    }

    /**
     * Save a agentStatus.
     *
     * @param agentStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentStatusDTO save(AgentStatusDTO agentStatusDTO) {
        LOG.debug("Request to save AgentStatus : {}", agentStatusDTO);
        AgentStatus agentStatus = agentStatusMapper.toEntity(agentStatusDTO);
        agentStatus = agentStatusRepository.save(agentStatus);
        return agentStatusMapper.toDto(agentStatus);
    }

    /**
     * Update a agentStatus.
     *
     * @param agentStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentStatusDTO update(AgentStatusDTO agentStatusDTO) {
        LOG.debug("Request to update AgentStatus : {}", agentStatusDTO);
        AgentStatus agentStatus = agentStatusMapper.toEntity(agentStatusDTO);
        agentStatus = agentStatusRepository.save(agentStatus);
        return agentStatusMapper.toDto(agentStatus);
    }

    /**
     * Partially update a agentStatus.
     *
     * @param agentStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AgentStatusDTO> partialUpdate(AgentStatusDTO agentStatusDTO) {
        LOG.debug("Request to partially update AgentStatus : {}", agentStatusDTO);

        return agentStatusRepository
            .findById(agentStatusDTO.getId())
            .map(existingAgentStatus -> {
                agentStatusMapper.partialUpdate(existingAgentStatus, agentStatusDTO);

                return existingAgentStatus;
            })
            .map(agentStatusRepository::save)
            .map(agentStatusMapper::toDto);
    }

    /**
     * Get all the agentStatuses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AgentStatusDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AgentStatuses");
        return agentStatusRepository.findAll(pageable).map(agentStatusMapper::toDto);
    }

    /**
     * Get one agentStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AgentStatusDTO> findOne(UUID id) {
        LOG.debug("Request to get AgentStatus : {}", id);
        return agentStatusRepository.findById(id).map(agentStatusMapper::toDto);
    }

    /**
     * Delete the agentStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete AgentStatus : {}", id);
        agentStatusRepository.deleteById(id);
    }
}
