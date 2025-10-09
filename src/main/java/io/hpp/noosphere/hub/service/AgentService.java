package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.Agent;
import io.hpp.noosphere.hub.domain.User;
import io.hpp.noosphere.hub.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.AgentRepository;
import io.hpp.noosphere.hub.repository.UserRepository;
import io.hpp.noosphere.hub.service.dto.AgentDTO;
import io.hpp.noosphere.hub.service.dto.UserDTO;
import io.hpp.noosphere.hub.service.mapper.AgentMapper;
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
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.Agent}.
 */
@Service
@Transactional
public class AgentService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    public AgentService(AgentRepository agentRepository, AgentMapper agentMapper) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
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
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        agentDTO.setCreatedByUser(userDTO);
        agentDTO.setCreatedAt(timestamp);
        agentDTO.setStatusCode(StatusCode.ACTIVE);
        Agent agent = agentMapper.toEntity(agentDTO);
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
            User user = userService.findById(userId);
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
}
