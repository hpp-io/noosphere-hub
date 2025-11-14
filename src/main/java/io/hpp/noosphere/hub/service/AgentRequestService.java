package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.AgentRequest;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.AgentNotFoundException;
import io.hpp.noosphere.hub.exception.AgentRequestNotFoundException;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentRequestRepository;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.AgentRequestDTO;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserSubscriptionDTO;
import io.hpp.noosphere.hub.service.mapper.AgentRequestMapper;
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
 * Service Implementation for managing {@link AgentRequest}.
 */
@Service
@Transactional
public class AgentRequestService {

  private static final Logger LOG = LoggerFactory.getLogger(AgentRequestService.class);

  private final AgentRequestRepository agentRequestRepository;

  private final AgentRequestMapper agentRequestMapper;

  public AgentRequestService(AgentRequestRepository agentRequestRepository, AgentRequestMapper agentRequestMapper) {
    this.agentRequestRepository = agentRequestRepository;
    this.agentRequestMapper = agentRequestMapper;
  }

  public AgentRequestDTO create(
    AgentService agentService,
    String userId,
    UUID agentId,
    UUID containerId,
    UUID userSubscriptionId,
    Instant timestamp
  ) throws PermissionDeniedException, AgentNotFoundException {
    LOG.debug("Request to save Agent {}, Container {}", agentId, containerId);
    agentService.validateOwner(agentId, userId);
    AgentRequestDTO agentRequestDTO = new AgentRequestDTO();
    AgentDTO agentDTO = new AgentDTO();
    agentDTO.setId(agentId);
    agentRequestDTO.setAgent(agentDTO);
    ContainerDTO containerDTO = new ContainerDTO();
    containerDTO.setId(containerId);
    agentRequestDTO.setContainer(containerDTO);
    UserSubscriptionDTO userSubscriptionDTO = new UserSubscriptionDTO();
    userSubscriptionDTO.setId(userSubscriptionId);
    agentRequestDTO.setUserSubscription(userSubscriptionDTO);

    agentRequestDTO.setCreatedAt(timestamp);
    agentRequestDTO.setStatusCode(StatusCode.ACTIVE);
    AgentRequest agentRequest = agentRequestMapper.toEntity(agentRequestDTO);
    agentRequest = agentRequestRepository.save(agentRequest);
    return agentRequestMapper.toDto(agentRequest);
  }

  public AgentRequestDTO update(AgentService agentService, String userId, UUID id, StatusCode statusCode, Instant timestamp)
    throws PermissionDeniedException, AgentNotFoundException, AgentRequestNotFoundException {
    LOG.debug("Request to update AgentRequest {}", id);
    AgentRequest agentRequest = agentRequestRepository.findById(id).orElseThrow(() -> new AgentRequestNotFoundException(id.toString()));
    agentService.validateOwner(agentRequest.getAgent().getId(), userId);
    agentRequest.setUpdatedAt(timestamp);
    agentRequest.setStatusCode(statusCode);
    agentRequest = agentRequestRepository.save(agentRequest);
    return agentRequestMapper.toDto(agentRequest);
  }

  /**
   * Get all the agentRequests.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<AgentRequestDTO> search(UUID containerId, UUID agentId, String agentName, StatusCode statusCode, Pageable pageable) {
    LOG.debug("Request to search AgentRequests");
    return agentRequestRepository.search(containerId, agentId, agentName, statusCode, pageable).map(agentRequestMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<AgentRequestDTO> findActiveByAgentId(UUID agentId, Pageable pageable) {
    LOG.debug("Request to search AgentRequests");
    return agentRequestRepository.findActiveByAgentId(agentId, pageable).map(agentRequestMapper::toDto);
  }

  /**
   * Get one agentRequest by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<AgentRequestDTO> findOne(UUID id) {
    LOG.debug("Request to get AgentRequest : {}", id);
    return agentRequestRepository.findById(id).map(agentRequestMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Optional<AgentRequestDTO> findByAgentIdAndContainerId(UUID agentId, UUID containerId) {
    LOG.debug("Request to get Agent {}, Container {}", agentId, containerId);
    return agentRequestRepository.findByAgentIdAndContainerId(agentId, containerId).map(agentRequestMapper::toDto);
  }

  public void delete(AgentService agentService, String userId, UUID id)
    throws PermissionDeniedException, AgentRequestNotFoundException, AgentNotFoundException {
    LOG.debug("Request to delete AgentRequest : {}", id);
    AgentRequest agentRequest = agentRequestRepository.findById(id).orElseThrow(() -> new AgentRequestNotFoundException(id.toString()));
    agentService.validateOwner(agentRequest.getAgent().getId(), userId);
    agentRequestRepository.delete(agentRequest);
  }
}
