package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.DEFAULT_LANGUAGE;

import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.Validator;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.ValidatorRepository;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.dto.ValidatorDTO;
import io.hpp.noosphere.hub.service.mapper.ValidatorMapper;
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
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.Validator}.
 */
@Service
@Transactional
public class ValidatorService {

  private static final Logger LOG = LoggerFactory.getLogger(ValidatorService.class);

  private final ValidatorRepository validatorRepository;
  private final UserService userService;
  private final ValidatorMapper validatorMapper;

  public ValidatorService(ValidatorRepository validatorRepository, ValidatorMapper validatorMapper,
    UserService userService
  ) {
    this.validatorRepository = validatorRepository;
    this.validatorMapper = validatorMapper;
    this.userService = userService;
  }

  public Validator validateOwner(UUID id, String userId) throws PermissionDeniedException {
    Validator validator = null;
    Optional<Validator> optionalData = validatorRepository.findById(id);
    if (optionalData.isPresent()) {
      validator = optionalData.get();
      if (validator.getCreatedByUser() == null || !userId.equals(validator.getCreatedByUser().getId())) {
        throw new PermissionDeniedException(id.toString());
      }
    }
    return validator;
  }

  /**
   * Save a validator.
   *
   * @param validatorDTO the entity to save.
   * @return the persisted entity.
   */
  public ValidatorDTO create(String userId, ValidatorDTO validatorDTO, Instant timestamp) {
    LOG.debug("Request to save Validator : {}", validatorDTO);
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    validatorDTO.setCreatedByUser(userDTO);
    validatorDTO.setCreatedAt(timestamp);
    validatorDTO.setStatusCode(StatusCode.ACTIVE);
    Validator validator = validatorMapper.toEntity(validatorDTO);
    validator = validatorRepository.save(validator);
    return validatorMapper.toDto(validator);
  }

  public ValidatorDTO register(String name, String apiKey, String walletAddress, String verifierAddress, String email, Instant timestamp) {
    LOG.debug("Request to register Validator");
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
    Validator validator = new Validator();
    validator.setCreatedByUser(createdByUser);
    validator.setName(name);
    validator.setCreatedAt(timestamp);
    validator.setStatusCode(StatusCode.ACTIVE);
    validator.setWalletAddress(walletAddress);
    validator.setVerifierAddress(verifierAddress);
    validator = validatorRepository.save(validator);
    return validatorMapper.toDto(validator);
  }

  public ValidatorDTO partialUpdate(UserService userService, String userId, ValidatorDTO validatorDTO, Instant timestamp)
    throws PermissionDeniedException {
    LOG.debug("Request to partially update Validator : {}", validatorDTO);
    Validator validator = this.validateOwner(validatorDTO.getId(), userId);
    if (validator != null) {
      validatorMapper.partialUpdate(validator, validatorDTO);
      validator.setUpdatedAt(timestamp);
      User user = userService.findEntityById(userId);
      validator.setUpdatedByUser(user);
      validator = validatorRepository.save(validator);
      return validatorMapper.toDto(validator);
    } else {
      return null;
    }
  }

  /**
   * Search validators.
   *
   * @param pageable the pagination information.
   * @return the list of entities.
   */
  @Transactional(readOnly = true)
  public Page<ValidatorDTO> search(String searchText, String name, StatusCode statusCode, String createdByUserId, String walletAddress, String verifierAddress, Pageable pageable) {
    LOG.debug("Request to search all Validators");
    return validatorRepository.search(searchText, name, statusCode, createdByUserId, walletAddress, verifierAddress, pageable).map(validatorMapper::toDto);
  }

  /**
   * Get one validator by id.
   *
   * @param id the id of the entity.
   * @return the entity.
   */
  @Transactional(readOnly = true)
  public Optional<ValidatorDTO> findOne(UUID id) {
    LOG.debug("Request to get Validator : {}", id);
    //    Validator validator = this.validateOwner(id, userId);
    //    return Optional.ofNullable(validator != null ? validatorMapper.toDto(validator) : null);
    return validatorRepository.findById(id).map(validatorMapper::toDto);
  }

  /**
   * Delete the validator by id.
   *
   * @param id the id of the entity.
   */
  public void delete(String userId, UUID id) throws PermissionDeniedException {
    LOG.debug("Request to delete Validator : {}", id);
    Validator validator = this.validateOwner(id, userId);
    if (validator != null) {
      validatorRepository.delete(validator);
    }
  }

  public ValidatorDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) {
    LOG.debug("Request to update Validator status : {}", id);
    Optional<Validator> validatorOptional = validatorRepository.findById(id);
    if (validatorOptional.isPresent()) {
      Validator validator = validatorOptional.get();
      validator.setStatusCode(statusCode);
      validator.setUpdatedAt(timestamp);
      validator = validatorRepository.save(validator);
      return validatorMapper.toDto(validator);
    }
    return null;
  }

}
