package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.DEFAULT_LANGUAGE;

import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.Verifier;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.exception.VerifierNotFoundException;
import io.hpp.noosphere.hub.repository.VerifierRepository;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.dto.VerifierDTO;
import io.hpp.noosphere.hub.service.mapper.VerifierMapper;
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
 * Service Implementation for managing {@link Verifier}.
 */
@Service
@Transactional
public class VerifierService {

  private static final Logger LOG = LoggerFactory.getLogger(VerifierService.class);

  private final VerifierRepository verifierRepository;
  private final UserService userService;
  private final VerifierMapper verifierMapper;

  public VerifierService(VerifierRepository verifierRepository, VerifierMapper verifierMapper, UserService userService) {
    this.verifierRepository = verifierRepository;
    this.verifierMapper = verifierMapper;
    this.userService = userService;
  }

  public Verifier validateOwner(UUID id, String userId) throws PermissionDeniedException, VerifierNotFoundException {
    Verifier verifier = verifierRepository.findById(id).orElseThrow(() -> new VerifierNotFoundException(id.toString()));
    if (verifier.getCreatedByUser() == null || !userId.equals(verifier.getCreatedByUser().getId())) {
      throw new PermissionDeniedException(id.toString());
    }
    return verifier;
  }

  /**
   * Save a verifier.
   *
   * @param verifierDTO the entity to save.
   * @return the persisted entity.
   */
  public VerifierDTO create(String userId, VerifierDTO verifierDTO, Instant timestamp) {
    LOG.debug("Request to save Verifier : {}", verifierDTO);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    verifierDTO.setCreatedByUser(userDTO);
    verifierDTO.setCreatedAt(timestamp);
    verifierDTO.setStatusCode(StatusCode.ACTIVE);
    Verifier verifier = verifierMapper.toEntity(verifierDTO);
    verifier = verifierRepository.save(verifier);
    return verifierMapper.toDto(verifier);
  }

  public VerifierDTO register(String name, String apiKey, String walletAddress, String verifierAddress, String email, Instant timestamp) {
    LOG.debug("Request to register Verifier");
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
    Verifier verifier = new Verifier();
    verifier.setCreatedByUser(createdByUser);
    verifier.setName(name);
    verifier.setCreatedAt(timestamp);
    verifier.setStatusCode(StatusCode.ACTIVE);
    verifier.setWalletAddress(walletAddress);
    verifier.setVerifierAddress(verifierAddress);
    verifier = verifierRepository.save(verifier);
    return verifierMapper.toDto(verifier);
  }

  public VerifierDTO partialUpdate(UserService userService, String userId, VerifierDTO verifierDTO, Instant timestamp)
    throws PermissionDeniedException, VerifierNotFoundException {
    LOG.debug("Request to partially update Verifier : {}", verifierDTO);
    Verifier verifier = this.validateOwner(verifierDTO.getId(), userId);
    if (verifier != null) {
      verifierMapper.partialUpdate(verifier, verifierDTO);
      verifier.setUpdatedAt(timestamp);
      User user = userService.findEntityById(userId);
      verifier.setUpdatedByUser(user);
      verifier = verifierRepository.save(verifier);
      return verifierMapper.toDto(verifier);
    } else {
      return null;
    }
  }

  /**
   * Search verifiers.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<VerifierDTO> search(
    String searchText,
    String name,
    StatusCode statusCode,
    String createdByUserId,
    String walletAddress,
    String verifierAddress,
    Pageable pageable
  ) {
    LOG.debug("Request to search all Verifiers");
    return verifierRepository
      .search(searchText, name, statusCode, createdByUserId, walletAddress, verifierAddress, pageable)
      .map(verifierMapper::toDto);
  }

  /**
   * Get one verifier by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<VerifierDTO> findOne(UUID id) {
    LOG.debug("Request to get Verifier : {}", id);
    //    Verifier verifier = this.validateOwner(id, userId);
    //    return Optional.ofNullable(verifier != null ? verifierMapper.toDto(verifier) : null);
    return verifierRepository.findById(id).map(verifierMapper::toDto);
  }

  /**
   * Delete the verifier by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String userId, UUID id) throws PermissionDeniedException, VerifierNotFoundException {
    LOG.debug("Request to delete Verifier : {}", id);
    Verifier verifier = this.validateOwner(id, userId);
    if (verifier != null) {
      verifierRepository.delete(verifier);
    }
  }

  public VerifierDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) throws VerifierNotFoundException {
    LOG.debug("Request to update Verifier status : {}", id);
    Verifier verifier = verifierRepository.findById(id).orElseThrow(() -> new VerifierNotFoundException(id.toString()));
    verifier.setStatusCode(statusCode);
    verifier.setUpdatedAt(timestamp);
    verifier = verifierRepository.save(verifier);
    return verifierMapper.toDto(verifier);
  }
}
