package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.NodeStatus;
import io.hpp.noosphere.hub.repository.NodeStatusRepository;
import io.hpp.noosphere.hub.service.dto.NodeStatusDTO;
import io.hpp.noosphere.hub.service.mapper.NodeStatusMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.NodeStatus}.
 */
@Service
@Transactional
public class NodeStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeStatusService.class);

    private final NodeStatusRepository nodeStatusRepository;

    private final NodeStatusMapper nodeStatusMapper;

    public NodeStatusService(NodeStatusRepository nodeStatusRepository, NodeStatusMapper nodeStatusMapper) {
        this.nodeStatusRepository = nodeStatusRepository;
        this.nodeStatusMapper = nodeStatusMapper;
    }

    /**
     * Save a nodeStatus.
     *
     * @param nodeStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeStatusDTO save(NodeStatusDTO nodeStatusDTO) {
        LOG.debug("Request to save NodeStatus : {}", nodeStatusDTO);
        NodeStatus nodeStatus = nodeStatusMapper.toEntity(nodeStatusDTO);
        nodeStatus = nodeStatusRepository.save(nodeStatus);
        return nodeStatusMapper.toDto(nodeStatus);
    }

    /**
     * Update a nodeStatus.
     *
     * @param nodeStatusDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeStatusDTO update(NodeStatusDTO nodeStatusDTO) {
        LOG.debug("Request to update NodeStatus : {}", nodeStatusDTO);
        NodeStatus nodeStatus = nodeStatusMapper.toEntity(nodeStatusDTO);
        nodeStatus = nodeStatusRepository.save(nodeStatus);
        return nodeStatusMapper.toDto(nodeStatus);
    }

    /**
     * Partially update a nodeStatus.
     *
     * @param nodeStatusDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NodeStatusDTO> partialUpdate(NodeStatusDTO nodeStatusDTO) {
        LOG.debug("Request to partially update NodeStatus : {}", nodeStatusDTO);

        return nodeStatusRepository
            .findById(nodeStatusDTO.getId())
            .map(existingNodeStatus -> {
                nodeStatusMapper.partialUpdate(existingNodeStatus, nodeStatusDTO);

                return existingNodeStatus;
            })
            .map(nodeStatusRepository::save)
            .map(nodeStatusMapper::toDto);
    }

    /**
     * Get all the nodeStatuses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NodeStatusDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NodeStatuses");
        return nodeStatusRepository.findAll(pageable).map(nodeStatusMapper::toDto);
    }

    /**
     * Get one nodeStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NodeStatusDTO> findOne(UUID id) {
        LOG.debug("Request to get NodeStatus : {}", id);
        return nodeStatusRepository.findById(id).map(nodeStatusMapper::toDto);
    }

    /**
     * Delete the nodeStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete NodeStatus : {}", id);
        nodeStatusRepository.deleteById(id);
    }
}
