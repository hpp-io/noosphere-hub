package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.COLUMN_NAME_ID;

import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.UserSubscription;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.exception.UserSubscriptionNotFoundException;
import io.hpp.noosphere.hub.repository.UserSubscriptionRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

    public UserSubscriptionService(
        UserSubscriptionRepository userSubscriptionRepository,
        UserSubscriptionMapper userSubscriptionMapper,
        UserService userService
    ) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.userSubscriptionMapper = userSubscriptionMapper;
        this.userService = userService;
    }

    public UserSubscription validateOwner(UUID id, String userId) throws PermissionDeniedException, UserSubscriptionNotFoundException {
        UserSubscription userSubscription = userSubscriptionRepository
            .findById(id)
            .orElseThrow(() -> new UserSubscriptionNotFoundException(id.toString()));
        if (userSubscription.getOwner() == null || !userId.equals(userSubscription.getOwner().getId())) {
            throw new PermissionDeniedException(id.toString());
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

    public UserSubscriptionDTO partialUpdate(
        UserService userService,
        String userId,
        UserSubscriptionDTO userSubscriptionDTO,
        Instant timestamp
    ) throws PermissionDeniedException, UserSubscriptionNotFoundException {
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
    public Page<UserSubscriptionDTO> search(
        String userId,
        List<UUID> containerIdList,
        StatusCode statusCode,
        String containerName,
        Pageable pageable
    ) {
        LOG.debug("Request to search all UserSubscriptions");
        return userSubscriptionRepository
            .search(userId, containerIdList, statusCode, containerName, pageable)
            .map(userSubscriptionMapper::toDto);
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
    public void delete(String userId, UUID id) throws PermissionDeniedException, UserSubscriptionNotFoundException {
        LOG.debug("Request to delete UserSubscription : {}", id);
        UserSubscription userSubscription = this.validateOwner(id, userId);
        if (userSubscription != null) {
            userSubscriptionRepository.delete(userSubscription);
        }
    }

    public UserSubscriptionDTO updateStatus(UUID id, StatusCode statusCode, Instant timestamp) throws UserSubscriptionNotFoundException {
        LOG.debug("Request to update UserSubscription status : {}", id);
        UserSubscription userSubscription = userSubscriptionRepository
            .findById(id)
            .orElseThrow(() -> new UserSubscriptionNotFoundException(id.toString()));
        userSubscription.setStatusCode(statusCode);
        userSubscription.setUpdatedAt(timestamp);
        userSubscription = userSubscriptionRepository.save(userSubscription);
        return userSubscriptionMapper.toDto(userSubscription);
    }

    @Transactional(readOnly = true)
    public List<UserSubscriptionDTO> findAllByAgentId(AgentContainerService agentContainerService, UUID agentId, Integer recordSize) {
        List<UserSubscriptionDTO> list = new ArrayList<>();
        List<UUID> containerIdList = agentContainerService.findAllContainerIdListByAgentId(agentId, StatusCode.ACTIVE);
        if (!containerIdList.isEmpty()) {
            Pageable pageableSubscription = PageRequest.of(0, recordSize != null ? recordSize : 10, Sort.by(Direction.ASC, COLUMN_NAME_ID));
            this.search(null, containerIdList, StatusCode.ACTIVE, null, pageableSubscription).forEach(list::add);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Long countAllByAgentId(AgentContainerService agentContainerService, UUID agentId) {
        Long count = 0L;
        List<UUID> containerIdList = agentContainerService.findAllContainerIdListByAgentId(agentId, StatusCode.ACTIVE);
        if (!containerIdList.isEmpty()) {
            count = userSubscriptionRepository.countAllUserSubscriptionIdListByContainerIdList(containerIdList, StatusCode.ACTIVE);
        }
        return count;
    }
}
