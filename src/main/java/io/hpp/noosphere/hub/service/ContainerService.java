package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.DEFAULT_LANGUAGE;

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

  private final UserService userService;
  private final ContainerRepository containerRepository;

  private final ContainerMapper containerMapper;

  public ContainerService(ContainerRepository containerRepository, ContainerMapper containerMapper,
    UserService userService
  ) {
    this.containerRepository = containerRepository;
    this.containerMapper = containerMapper;
    this.userService = userService;
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


  public ContainerDTO register(String name, String apiKey, String walletAddress, String email, Instant timestamp) {
    LOG.debug("Request to register Container");
    UserDTO userDTO = userService.findOneByEmailAndWalletAddressOrApiKey(email, walletAddress, apiKey, null);
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
    Container container = new Container();
    container.setCreatedByUser(createdByUser);
    container.setName(name);
    container.setCreatedAt(timestamp);
    container.setStatusCode(StatusCode.ACTIVE);
    container.setWalletAddress(walletAddress);
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
      User user = userService.findEntityById(userId);
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
  public Page<ContainerDTO> search(String name, StatusCode statusCode, String createdByUserId, String walletAddress, Pageable pageable) {
    LOG.debug("Request to search Containers");
    return containerRepository.search(name, statusCode, createdByUserId, walletAddress, pageable).map(containerMapper::toDto);
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

  public ContainerDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) {
    LOG.debug("Request to update Container status : {}", id);
    Optional<Container> containerOptional = containerRepository.findById(id);
    if (containerOptional.isPresent()) {
      Container container = containerOptional.get();
      container.setStatusCode(statusCode);
      container.setUpdatedAt(timestamp);
      container = containerRepository.save(container);
      return containerMapper.toDto(container);
    }
    return null;
  }
}
