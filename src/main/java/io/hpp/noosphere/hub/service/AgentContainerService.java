package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.AgentContainer;
import io.hpp.noosphere.hub.repository.AgentContainerRepository;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.mapper.AgentContainerMapper;
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

    /**
     * Save a agentContainer.
     *
     * @param agentContainerDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentContainerDTO save(AgentContainerDTO agentContainerDTO) {
        LOG.debug("Request to save AgentContainer : {}", agentContainerDTO);
        AgentContainer agentContainer = agentContainerMapper.toEntity(agentContainerDTO);
        agentContainer = agentContainerRepository.save(agentContainer);
        return agentContainerMapper.toDto(agentContainer);
    }

    /**
     * Update a agentContainer.
     *
     * @param agentContainerDTO the entity to save.
     * @return the persisted entity.
     */
    public AgentContainerDTO update(AgentContainerDTO agentContainerDTO) {
        LOG.debug("Request to update AgentContainer : {}", agentContainerDTO);
        AgentContainer agentContainer = agentContainerMapper.toEntity(agentContainerDTO);
        agentContainer = agentContainerRepository.save(agentContainer);
        return agentContainerMapper.toDto(agentContainer);
    }

    /**
     * Partially update a agentContainer.
     *
     * @param agentContainerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AgentContainerDTO> partialUpdate(AgentContainerDTO agentContainerDTO) {
        LOG.debug("Request to partially update AgentContainer : {}", agentContainerDTO);

        return agentContainerRepository
            .findById(agentContainerDTO.getId())
            .map(existingAgentContainer -> {
                agentContainerMapper.partialUpdate(existingAgentContainer, agentContainerDTO);

                return existingAgentContainer;
            })
            .map(agentContainerRepository::save)
            .map(agentContainerMapper::toDto);
    }

    /**
     * Get all the agentContainers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AgentContainerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AgentContainers");
        return agentContainerRepository.findAll(pageable).map(agentContainerMapper::toDto);
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

    /**
     * Delete the agentContainer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete AgentContainer : {}", id);
        agentContainerRepository.deleteById(id);
    }
}
