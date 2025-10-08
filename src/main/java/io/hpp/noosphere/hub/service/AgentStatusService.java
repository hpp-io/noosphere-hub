package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.AgentStatus;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.AgentNotFoundException;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.repository.AgentStatusRepository;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import io.hpp.noosphere.hub.service.mapper.AgentStatusMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
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
  private final AgentRepository agentRepository;

  private final AgentStatusMapper agentStatusMapper;

  public AgentStatusService(AgentStatusRepository agentStatusRepository, AgentStatusMapper agentStatusMapper,
    AgentRepository agentRepository) {
    this.agentStatusRepository = agentStatusRepository;
    this.agentStatusMapper = agentStatusMapper;
    this.agentRepository = agentRepository;
  }

  /**
   * Save a agentStatus.
   *
   * @param agentStatusDTO the entity to save.
   * @return the persisted entity.
   */
  public AgentStatusDTO save(AgentStatusDTO agentStatusDTO, Instant timestamp) {
    LOG.debug("Request to save AgentStatus : {}", agentStatusDTO);
    if (agentStatusDTO.getId() == null) {
      agentStatusDTO.setCreatedAt(timestamp);
    }
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

  public AgentStatusDTO updateKeepAlive(String userId, UUID agentId, Instant timestamp) throws AgentNotFoundException, PermissionDeniedException {
    Optional<Agent> optionalAgent = agentRepository.findById(agentId);
    AgentStatus agentStatus = null;
    if (optionalAgent.isPresent()) {
      Agent agent = optionalAgent.get();
      if (agent.getCreatedByUser() == null || !userId.equals(agent.getCreatedByUser().getId())) {
        throw new PermissionDeniedException(agentId.toString());
      }
      if (!StatusCode.ACTIVE.equals(agent.getStatusCode())) {
        agent.setStatusCode(StatusCode.ACTIVE);
        agent = agentRepository.save(agent);
      }
      Optional<AgentStatus> optionalAgentStatus = agentStatusRepository.findByAgentId(agentId);
      if (optionalAgentStatus.isPresent()) {
        agentStatus = optionalAgentStatus.get();
        agentStatus.setLastKeepAliveAt(timestamp);
        agentStatus = agentStatusRepository.save(agentStatus);
      } else {
        agentStatus = new AgentStatus();
        agentStatus.setAgent(agent);
        agentStatus.setCreatedAt(timestamp);
        agentStatus.setLastKeepAliveAt(timestamp);
        agentStatus = agentStatusRepository.save(agentStatus);
      }
    } else {
      throw new AgentNotFoundException(agentId.toString());
    }
    return agentStatusMapper.toDto(agentStatus);
  }

  /**
   * Get all the agentStatuses.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<AgentStatusDTO> search(String agentName, StatusCode agentStatusCode, Pageable pageable) {
    LOG.debug("Request to search AgentStatuses");
    return agentStatusRepository.search(agentName, agentStatusCode, pageable).map(agentStatusMapper::toDto);
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
