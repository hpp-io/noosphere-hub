package io.hpp.noosphere.hub.service;

import io.hpp.noosphere.hub.domain.NodeContainer;
import io.hpp.noosphere.hub.repository.NodeContainerRepository;
import io.hpp.noosphere.hub.service.dto.NodeContainerDTO;
import io.hpp.noosphere.hub.service.mapper.NodeContainerMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.hpp.noosphere.hub.domain.NodeContainer}.
 */
@Service
@Transactional
public class NodeContainerService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeContainerService.class);

    private final NodeContainerRepository nodeContainerRepository;

    private final NodeContainerMapper nodeContainerMapper;

    public NodeContainerService(NodeContainerRepository nodeContainerRepository, NodeContainerMapper nodeContainerMapper) {
        this.nodeContainerRepository = nodeContainerRepository;
        this.nodeContainerMapper = nodeContainerMapper;
    }

    /**
     * Save a nodeContainer.
     *
     * @param nodeContainerDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeContainerDTO save(NodeContainerDTO nodeContainerDTO) {
        LOG.debug("Request to save NodeContainer : {}", nodeContainerDTO);
        NodeContainer nodeContainer = nodeContainerMapper.toEntity(nodeContainerDTO);
        nodeContainer = nodeContainerRepository.save(nodeContainer);
        return nodeContainerMapper.toDto(nodeContainer);
    }

    /**
     * Update a nodeContainer.
     *
     * @param nodeContainerDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeContainerDTO update(NodeContainerDTO nodeContainerDTO) {
        LOG.debug("Request to update NodeContainer : {}", nodeContainerDTO);
        NodeContainer nodeContainer = nodeContainerMapper.toEntity(nodeContainerDTO);
        nodeContainer = nodeContainerRepository.save(nodeContainer);
        return nodeContainerMapper.toDto(nodeContainer);
    }

    /**
     * Partially update a nodeContainer.
     *
     * @param nodeContainerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NodeContainerDTO> partialUpdate(NodeContainerDTO nodeContainerDTO) {
        LOG.debug("Request to partially update NodeContainer : {}", nodeContainerDTO);

        return nodeContainerRepository
            .findById(nodeContainerDTO.getId())
            .map(existingNodeContainer -> {
                nodeContainerMapper.partialUpdate(existingNodeContainer, nodeContainerDTO);

                return existingNodeContainer;
            })
            .map(nodeContainerRepository::save)
            .map(nodeContainerMapper::toDto);
    }

    /**
     * Get all the nodeContainers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NodeContainerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all NodeContainers");
        return nodeContainerRepository.findAll(pageable).map(nodeContainerMapper::toDto);
    }

    /**
     * Get one nodeContainer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NodeContainerDTO> findOne(UUID id) {
        LOG.debug("Request to get NodeContainer : {}", id);
        return nodeContainerRepository.findById(id).map(nodeContainerMapper::toDto);
    }

    /**
     * Delete the nodeContainer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete NodeContainer : {}", id);
        nodeContainerRepository.deleteById(id);
    }
}
