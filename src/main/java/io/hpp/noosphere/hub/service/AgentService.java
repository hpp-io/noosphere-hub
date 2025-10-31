package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.DEFAULT_LANGUAGE;
import static io.hpp.noosphere.hub.config.Constants.PROPERTY_NAME_API_KEY;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.AgentNotFoundException;
import io.hpp.noosphere.hub.exception.AlreadyExistsException;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.mapper.AgentMapper;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgentService {

  private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);

  private final AgentRepository agentRepository;
  private final UserService userService;
  private final AgentMapper agentMapper;

  public AgentService(AgentRepository agentRepository, AgentMapper agentMapper,
    UserService userService
  ) {
    this.agentRepository = agentRepository;
    this.agentMapper = agentMapper;
    this.userService = userService;
  }

  public Agent validateOwner(UUID id, String userId) throws PermissionDeniedException {
    Agent agent = null;
    Optional<Agent> optionalData = agentRepository.findById(id);
    if (optionalData.isPresent()) {
      agent = optionalData.get();
      if (agent.getCreatedByUser() == null || !userId.equals(agent.getCreatedByUser().getId())) {
        throw new PermissionDeniedException(id.toString());
      }
    }
    return agent;
  }

  /**
   * Save a agent.
   *
   * @param agentDTO the entity to save.
   * @return the persisted entity.
   */
  public AgentDTO create(String userId, AgentDTO agentDTO, Instant timestamp) {
    LOG.debug("Request to save Agent : {}", agentDTO);
    if (CommonUtils.isValid(agentDTO.getApiKey())){
      if (this.findOneByApiKey(agentDTO.getApiKey()).isPresent()) {
        throw new AlreadyExistsException(PROPERTY_NAME_API_KEY, agentDTO.getApiKey());
      }
    }
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    agentDTO.setCreatedByUser(userDTO);
    agentDTO.setCreatedAt(timestamp);
    agentDTO.setStatusCode(StatusCode.ACTIVE);
    Agent agent = agentMapper.toEntity(agentDTO);
    agent = agentRepository.save(agent);
    return agentMapper.toDto(agent);
  }

  public AgentDTO register(String name, String apiKey, String walletAddress, String email, Instant timestamp) {
    LOG.debug("Request to register Agent");
    if (CommonUtils.isValid(apiKey)){
      if (this.findOneByApiKey(apiKey).isPresent()) {
        throw new AlreadyExistsException(PROPERTY_NAME_API_KEY, apiKey);
      }
    }
    UserDTO userDTO = userService.findOneByEmailOrWalletAddressOrApiKey(email, walletAddress, apiKey, null);
    if (userDTO == null) {
      userDTO = new UserDTO();
      userDTO.setEmail(email);
      userDTO.setWalletAddress(walletAddress);
      userDTO.setApiKey(apiKey);
      userDTO.setLangKey(DEFAULT_LANGUAGE);
      userDTO = userService.createKeycloakUser(userDTO);
    }
    User createdByUser = new User();
    createdByUser.setId(userDTO.getId());
    Agent agent = new Agent();
    agent.setCreatedByUser(createdByUser);
    agent.setName(name);
    agent.setCreatedAt(timestamp);
    agent.setStatusCode(StatusCode.ACTIVE);
    agent.setApiKey(apiKey);
    agent = agentRepository.save(agent);
    return agentMapper.toDto(agent);
  }

  public AgentDTO partialUpdate(UserService userService, String userId, AgentDTO agentDTO, Instant timestamp)
    throws PermissionDeniedException {
    LOG.debug("Request to partially update Agent : {}", agentDTO);
    Agent agent = this.validateOwner(agentDTO.getId(), userId);
    if (agent != null) {
      agentMapper.partialUpdate(agent, agentDTO);
      agent.setUpdatedAt(timestamp);
      User user = userService.findEntityById(userId);
      agent.setUpdatedByUser(user);
      agent = agentRepository.save(agent);
      return agentMapper.toDto(agent);
    } else {
      return null;
    }
  }

  /**
   * Search agents.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<AgentDTO> search(String name, StatusCode statusCode, String createdByUserId, Pageable pageable) {
    LOG.debug("Request to search all Agents");
    return agentRepository.search(name, statusCode, createdByUserId, pageable).map(agentMapper::toDto);
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
    //    Agent agent = this.validateOwner(id, userId);
    //    return Optional.ofNullable(agent != null ? agentMapper.toDto(agent) : null);
    return agentRepository.findById(id).map(agentMapper::toDto);
  }

  /**
   * Delete the agent by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String userId, UUID id) throws PermissionDeniedException {
    LOG.debug("Request to delete Agent : {}", id);
    Agent agent = this.validateOwner(id, userId);
    if (agent != null) {
      agentRepository.delete(agent);
    }
  }

  public AgentDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) {
    LOG.debug("Request to update Agent status : {}", id);
    Optional<Agent> agentOptional = agentRepository.findById(id);
    if (agentOptional.isPresent()) {
      Agent agent = agentOptional.get();
      agent.setStatusCode(statusCode);
      agent.setUpdatedAt(timestamp);
      agent = agentRepository.save(agent);
      return agentMapper.toDto(agent);
    }
    return null;
  }

  @Transactional(readOnly = true)
  public Optional<AgentDTO> findOneByApiKey(String apiKey) {
    return agentRepository.findOneByApiKey(apiKey).map(agentMapper::toDto);
  }
}
