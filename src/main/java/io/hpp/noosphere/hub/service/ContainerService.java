package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.ContainerRepository;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.mapper.ContainerMapper;
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
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.Container}.
 */
@Service
@Transactional
public class ContainerService {

  private static final Logger LOG = LoggerFactory.getLogger(ContainerService.class);

  private final ContainerRepository containerRepository;

  private final ContainerMapper containerMapper;

  public ContainerService(ContainerRepository containerRepository, ContainerMapper containerMapper) {
    this.containerRepository = containerRepository;
    this.containerMapper = containerMapper;
  }


  public Container validateOwner(UUID id, String userId) throws PermissionDeniedException {
    Container container = null;
    Optional<Container> optionalData = containerRepository.findById(id);
    if (optionalData.isPresent()) {
      container = optionalData.get();
      if (container.getCreatedByUser() == null || !userId.equals(container.getCreatedByUser().getId())) {
        throw new PermissionDeniedException(id.toString());
      }
    }
    return container;
  }

  /**
   * Save a container.
   *
   * @param containerDTO the entity to save.
   * @return the persisted entity.
   */
  public ContainerDTO create(String userId, ContainerDTO containerDTO, Instant timestamp) {
    LOG.debug("Request to save Container : {}", containerDTO);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    containerDTO.setCreatedByUser(userDTO);
    containerDTO.setCreatedAt(timestamp);
    containerDTO.setStatusCode(StatusCode.ACTIVE);
    Container container = containerMapper.toEntity(containerDTO);
    container = containerRepository.save(container);
    return containerMapper.toDto(container);
  }

  /**
   * Partially update a container.
   *
   * @param containerDTO the entity to update partially.
   * @return the persisted entity.
   */
  public ContainerDTO partialUpdate(UserService userService, String userId, ContainerDTO containerDTO, Instant timestamp)
    throws PermissionDeniedException {
    LOG.debug("Request to partially update Container : {}", containerDTO);
    Container container = this.validateOwner(containerDTO.getId(), userId);
    if (container != null) {
      containerMapper.partialUpdate(container, containerDTO);
      container.setUpdatedAt(timestamp);
      User user = userService.findById(userId);
      container.setUpdatedByUser(user);
      container = containerRepository.save(container);
      return containerMapper.toDto(container);
    } else {
      return null;
    }
  }

  /**
   * Search containers.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<ContainerDTO> search(String name, StatusCode statusCode, String createdByUserId, Pageable pageable) {
    LOG.debug("Request to search Containers");
    return containerRepository.search(name, statusCode, createdByUserId, pageable).map(containerMapper::toDto);
  }

  /**
   * Get one container by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ContainerDTO> findOne(UUID id) {
    LOG.debug("Request to get Container : {}", id);
    return containerRepository.findById(id).map(containerMapper::toDto);
  }

  /**
   * Delete the container by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String userId, UUID id) throws PermissionDeniedException {
    LOG.debug("Request to delete Container : {}", id);
    Container container = this.validateOwner(id, userId);
    if (container != null) {
      containerRepository.delete(container);
    }
  }
}
