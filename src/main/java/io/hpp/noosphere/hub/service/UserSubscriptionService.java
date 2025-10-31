package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.UserSubscription;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.UserSubscriptionRepository;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.dto.UserSubscriptionDTO;
import io.hpp.noosphere.hub.service.mapper.UserSubscriptionMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link UserSubscription}.
 */
@Service
@Transactional
public class UserSubscriptionService {

  private static final Logger LOG = LoggerFactory.getLogger(UserSubscriptionService.class);

  private final UserSubscriptionRepository userSubscriptionRepository;
  private final UserService userService;
  private final UserSubscriptionMapper userSubscriptionMapper;

  public UserSubscriptionService(UserSubscriptionRepository userSubscriptionRepository, UserSubscriptionMapper userSubscriptionMapper,
    UserService userService
  ) {
    this.userSubscriptionRepository = userSubscriptionRepository;
    this.userSubscriptionMapper = userSubscriptionMapper;
    this.userService = userService;
  }

  public UserSubscription validateOwner(UUID id, String userId) throws PermissionDeniedException {
    UserSubscription userSubscription = null;
    Optional<UserSubscription> optionalData = userSubscriptionRepository.findById(id);
    if (optionalData.isPresent()) {
      userSubscription = optionalData.get();
      if (userSubscription.getOwner() == null || !userId.equals(userSubscription.getOwner().getId())) {
        throw new PermissionDeniedException(id.toString());
      }
    }
    return userSubscription;
  }

  /**
   * Save a userSubscription.
   *
   * @param userSubscriptionDTO the entity to save.
   * @return the persisted entity.
   */
  public UserSubscriptionDTO create(String userId, UserSubscriptionDTO userSubscriptionDTO, Instant timestamp) {
    LOG.debug("Request to save UserSubscription : {}", userSubscriptionDTO);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    userSubscriptionDTO.setOwner(userDTO);
    userSubscriptionDTO.setCreatedAt(timestamp);
    userSubscriptionDTO.setStatusCode(StatusCode.ACTIVE);
    UserSubscription userSubscription = userSubscriptionMapper.toEntity(userSubscriptionDTO);
    userSubscription = userSubscriptionRepository.save(userSubscription);
    return userSubscriptionMapper.toDto(userSubscription);
  }


  public UserSubscriptionDTO partialUpdate(UserService userService, String userId, UserSubscriptionDTO userSubscriptionDTO, Instant timestamp)
    throws PermissionDeniedException {
    LOG.debug("Request to partially update UserSubscription : {}", userSubscriptionDTO);
    UserSubscription userSubscription = this.validateOwner(userSubscriptionDTO.getId(), userId);
    if (userSubscription != null) {
      userSubscriptionMapper.partialUpdate(userSubscription, userSubscriptionDTO);
      userSubscription.setUpdatedAt(timestamp);
      User user = userService.findEntityById(userId);
      userSubscription = userSubscriptionRepository.save(userSubscription);
      return userSubscriptionMapper.toDto(userSubscription);
    } else {
      return null;
    }
  }

  /**
   * Search userSubscriptions.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<UserSubscriptionDTO> search(String userId, List<UUID> containerIdList, StatusCode statusCode, String containerName, Pageable pageable) {
    LOG.debug("Request to search all UserSubscriptions");
    return userSubscriptionRepository.search(userId, containerIdList, statusCode, containerName, pageable).map(userSubscriptionMapper::toDto);
  }

  /**
   * Get one userSubscription by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<UserSubscriptionDTO> findOne(UUID id) {
    LOG.debug("Request to get UserSubscription : {}", id);
    //    UserSubscription userSubscription = this.validateOwner(id, userId);
    //    return Optional.ofNullable(userSubscription != null ? userSubscriptionMapper.toDto(userSubscription) : null);
    return userSubscriptionRepository.findById(id).map(userSubscriptionMapper::toDto);
  }

  /**
   * Delete the userSubscription by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String userId, UUID id) throws PermissionDeniedException {
    LOG.debug("Request to delete UserSubscription : {}", id);
    UserSubscription userSubscription = this.validateOwner(id, userId);
    if (userSubscription != null) {
      userSubscriptionRepository.delete(userSubscription);
    }
  }

  public UserSubscriptionDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) {
    LOG.debug("Request to update UserSubscription status : {}", id);
    Optional<UserSubscription> userSubscriptionOptional = userSubscriptionRepository.findById(id);
    if (userSubscriptionOptional.isPresent()) {
      UserSubscription userSubscription = userSubscriptionOptional.get();
      userSubscription.setStatusCode(statusCode);
      userSubscription.setUpdatedAt(timestamp);
      userSubscription = userSubscriptionRepository.save(userSubscription);
      return userSubscriptionMapper.toDto(userSubscription);
    }
    return null;
  }


  public List<UserSubscriptionDTO> findAllByAgentId(AgentContainerService agentContainerService, UUID agentId) {
    List<UserSubscriptionDTO> list = new ArrayList<>();
    boolean allProcessed = false;
    Pageable pageableSubscription = PageRequest.of(0, 50);
    while (!allProcessed) {
      Pageable pageable = PageRequest.of(0, 10);
      Page<AgentContainerDTO> page = agentContainerService.search(agentId, null, StatusCode.ACTIVE, pageable);
      if (page.isEmpty()) {
        allProcessed = true;
      } else {
        List<UUID> containerIdList = new ArrayList<>();
        for (AgentContainerDTO dto : page) {
          if (dto != null && dto.getContainer() != null && dto.getContainer().getId() != null) {
            containerIdList.add(dto.getContainer().getId());
          }
        }
        if (!containerIdList.isEmpty()) {
          this.search(null, containerIdList, StatusCode.ACTIVE, null, pageableSubscription).forEach(list::add);
        }
      }
    }
    return list;
  }

}
