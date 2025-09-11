package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.NodeStatusAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.NodeStatus;
import io.hpp.noosphere.hub.repository.NodeStatusRepository;
import io.hpp.noosphere.hub.service.dto.NodeStatusDTO;
import io.hpp.noosphere.hub.service.mapper.NodeStatusMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NodeStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeStatusResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_KEEP_ALIVE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_KEEP_ALIVE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/node-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeStatusRepository nodeStatusRepository;

    @Autowired
    private NodeStatusMapper nodeStatusMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNodeStatusMockMvc;

    private NodeStatus nodeStatus;

    private NodeStatus insertedNodeStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NodeStatus createEntity() {
        return new NodeStatus().createdAt(DEFAULT_CREATED_AT).lastKeepAliveAt(DEFAULT_LAST_KEEP_ALIVE_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NodeStatus createUpdatedEntity() {
        return new NodeStatus().createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);
    }

    @BeforeEach
    void initTest() {
        nodeStatus = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNodeStatus != null) {
            nodeStatusRepository.delete(insertedNodeStatus);
            insertedNodeStatus = null;
        }
    }

    @Test
    @Transactional
    void createNodeStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);
        var returnedNodeStatusDTO = om.readValue(
            restNodeStatusMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeStatusDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NodeStatusDTO.class
        );

        // Validate the NodeStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNodeStatus = nodeStatusMapper.toEntity(returnedNodeStatusDTO);
        assertNodeStatusUpdatableFieldsEquals(returnedNodeStatus, getPersistedNodeStatus(returnedNodeStatus));

        insertedNodeStatus = returnedNodeStatus;
    }

    @Test
    @Transactional
    void createNodeStatusWithExistingId() throws Exception {
        // Create the NodeStatus with an existing ID
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeStatusMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        nodeStatus.setCreatedAt(null);

        // Create the NodeStatus, which fails.
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        restNodeStatusMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeStatusDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNodeStatuses() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        // Get all the nodeStatusList
        restNodeStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nodeStatus.getId().toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastKeepAliveAt").value(hasItem(DEFAULT_LAST_KEEP_ALIVE_AT.toString())));
    }

    @Test
    @Transactional
    void getNodeStatus() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        // Get the nodeStatus
        restNodeStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, nodeStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nodeStatus.getId().toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.lastKeepAliveAt").value(DEFAULT_LAST_KEEP_ALIVE_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNodeStatus() throws Exception {
        // Get the nodeStatus
        restNodeStatusMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNodeStatus() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeStatus
        NodeStatus updatedNodeStatus = nodeStatusRepository.findById(nodeStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNodeStatus are not directly saved in db
        em.detach(updatedNodeStatus);
        updatedNodeStatus.createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(updatedNodeStatus);

        restNodeStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeStatusDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNodeStatusToMatchAllProperties(updatedNodeStatus);
    }

    @Test
    @Transactional
    void putNonExistingNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeStatusDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNodeStatusWithPatch() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeStatus using partial update
        NodeStatus partialUpdatedNodeStatus = new NodeStatus();
        partialUpdatedNodeStatus.setId(nodeStatus.getId());

        partialUpdatedNodeStatus.createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);

        restNodeStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNodeStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNodeStatus))
            )
            .andExpect(status().isOk());

        // Validate the NodeStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNodeStatus, nodeStatus),
            getPersistedNodeStatus(nodeStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateNodeStatusWithPatch() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeStatus using partial update
        NodeStatus partialUpdatedNodeStatus = new NodeStatus();
        partialUpdatedNodeStatus.setId(nodeStatus.getId());

        partialUpdatedNodeStatus.createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);

        restNodeStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNodeStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNodeStatus))
            )
            .andExpect(status().isOk());

        // Validate the NodeStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeStatusUpdatableFieldsEquals(partialUpdatedNodeStatus, getPersistedNodeStatus(partialUpdatedNodeStatus));
    }

    @Test
    @Transactional
    void patchNonExistingNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nodeStatusDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNodeStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeStatus.setId(UUID.randomUUID());

        // Create the NodeStatus
        NodeStatusDTO nodeStatusDTO = nodeStatusMapper.toDto(nodeStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeStatusMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(nodeStatusDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NodeStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNodeStatus() throws Exception {
        // Initialize the database
        insertedNodeStatus = nodeStatusRepository.saveAndFlush(nodeStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the nodeStatus
        restNodeStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, nodeStatus.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return nodeStatusRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected NodeStatus getPersistedNodeStatus(NodeStatus nodeStatus) {
        return nodeStatusRepository.findById(nodeStatus.getId()).orElseThrow();
    }

    protected void assertPersistedNodeStatusToMatchAllProperties(NodeStatus expectedNodeStatus) {
        assertNodeStatusAllPropertiesEquals(expectedNodeStatus, getPersistedNodeStatus(expectedNodeStatus));
    }

    protected void assertPersistedNodeStatusToMatchUpdatableProperties(NodeStatus expectedNodeStatus) {
        assertNodeStatusAllUpdatablePropertiesEquals(expectedNodeStatus, getPersistedNodeStatus(expectedNodeStatus));
    }
}
