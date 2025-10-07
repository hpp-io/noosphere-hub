package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.mapper.AgentMapper;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.Agent}.
 */
@Service
@Transactional
public class AgentService {

  private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);

  private final AgentRepository agentRepository;

  private final AgentMapper agentMapper;

  public AgentService(
    AgentRepository agentRepository,
    AgentMapper agentMapper
    ) {
    this.agentRepository = agentRepository;
    this.agentMapper = agentMapper;
  }

  /**
   * Save a agent.
   *
   * @param agentDTO the entity to save.
   * @return the persisted entity.
   */
  public AgentDTO save(String userId, AgentDTO agentDTO, Instant timestamp) {
    LOG.debug("Request to save Agent : {}", agentDTO);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    if (agentDTO.getId() == null) {
      agentDTO.setCreatedByUser(userDTO);
      agentDTO.setCreatedAt(timestamp);
      agentDTO.setStatusCode(StatusCode.ACTIVE);
    } else {
      agentDTO.setUpdatedByUser(userDTO);
      agentDTO.setUpdatedAt(timestamp);
    }
    Agent agent = agentMapper.toEntity(agentDTO);
    agent = agentRepository.save(agent);
    return agentMapper.toDto(agent);
  }

  /**
   * Update a agent.
   *
   * @param agentDTO the entity to save.
   * @return the persisted entity.
   */
  public AgentDTO update(AgentDTO agentDTO) {
    LOG.debug("Request to update Agent : {}", agentDTO);
    Agent agent = agentMapper.toEntity(agentDTO);
    agent = agentRepository.save(agent);
    return agentMapper.toDto(agent);
  }

  /**
   * Partially update a agent.
   *
   * @param agentDTO the entity to update partially.
   * @return the persisted entity.
   */
  public Optional<AgentDTO> partialUpdate(AgentDTO agentDTO) {
    LOG.debug("Request to partially update Agent : {}", agentDTO);

    return agentRepository
      .findById(agentDTO.getId())
      .map(existingAgent -> {
        agentMapper.partialUpdate(existingAgent, agentDTO);

        return existingAgent;
      })
      .map(agentRepository::save)
      .map(agentMapper::toDto);
  }

  /**
   * Get all the agents.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<AgentDTO> findAll(Pageable pageable) {
    LOG.debug("Request to get all Agents");
    return agentRepository.findAll(pageable).map(agentMapper::toDto);
  }

  /**
   * Get all the agents where AgentStatus is {@code null}.
   *
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public List<AgentDTO> findAllWhereAgentStatusIsNull() {
    LOG.debug("Request to get all agents where AgentStatus is null");
    return StreamSupport.stream(agentRepository.findAll().spliterator(), false)
      .filter(agent -> agent.getAgentStatus() == null)
      .map(agentMapper::toDto)
      .collect(Collectors.toCollection(LinkedList::new));
  }

  /**
   * Get one agent by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<AgentDTO> findOne(UUID id) {
    LOG.debug("Request to get Agent : {}", id);
    return agentRepository.findById(id).map(agentMapper::toDto);
  }

  /**
   * Delete the agent by id.
   *
   * @param id the id of the entity.
   */
  public void delete(UUID id) {
    LOG.debug("Request to delete Agent : {}", id);
    agentRepository.deleteById(id);
  }
}
