package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.NodeContainerAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.NodeContainer;
import io.hpp.noosphere.hub.repository.NodeContainerRepository;
import io.hpp.noosphere.hub.service.dto.NodeContainerDTO;
import io.hpp.noosphere.hub.service.mapper.NodeContainerMapper;
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
 * Integration tests for the {@link NodeContainerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeContainerResourceIT {

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/node-containers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeContainerRepository nodeContainerRepository;

    @Autowired
    private NodeContainerMapper nodeContainerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNodeContainerMockMvc;

    private NodeContainer nodeContainer;

    private NodeContainer insertedNodeContainer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NodeContainer createEntity() {
        return new NodeContainer().statusCode(DEFAULT_STATUS_CODE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NodeContainer createUpdatedEntity() {
        return new NodeContainer().statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        nodeContainer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNodeContainer != null) {
            nodeContainerRepository.delete(insertedNodeContainer);
            insertedNodeContainer = null;
        }
    }

    @Test
    @Transactional
    void createNodeContainer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);
        var returnedNodeContainerDTO = om.readValue(
            restNodeContainerMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(nodeContainerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NodeContainerDTO.class
        );

        // Validate the NodeContainer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNodeContainer = nodeContainerMapper.toEntity(returnedNodeContainerDTO);
        assertNodeContainerUpdatableFieldsEquals(returnedNodeContainer, getPersistedNodeContainer(returnedNodeContainer));

        insertedNodeContainer = returnedNodeContainer;
    }

    @Test
    @Transactional
    void createNodeContainerWithExistingId() throws Exception {
        // Create the NodeContainer with an existing ID
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        nodeContainer.setStatusCode(null);

        // Create the NodeContainer, which fails.
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        restNodeContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        nodeContainer.setCreatedAt(null);

        // Create the NodeContainer, which fails.
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        restNodeContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNodeContainers() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        // Get all the nodeContainerList
        restNodeContainerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nodeContainer.getId().toString())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNodeContainer() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        // Get the nodeContainer
        restNodeContainerMockMvc
            .perform(get(ENTITY_API_URL_ID, nodeContainer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(nodeContainer.getId().toString()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNodeContainer() throws Exception {
        // Get the nodeContainer
        restNodeContainerMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNodeContainer() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeContainer
        NodeContainer updatedNodeContainer = nodeContainerRepository.findById(nodeContainer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNodeContainer are not directly saved in db
        em.detach(updatedNodeContainer);
        updatedNodeContainer.statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(updatedNodeContainer);

        restNodeContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeContainerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isOk());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNodeContainerToMatchAllProperties(updatedNodeContainer);
    }

    @Test
    @Transactional
    void putNonExistingNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeContainerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNodeContainerWithPatch() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeContainer using partial update
        NodeContainer partialUpdatedNodeContainer = new NodeContainer();
        partialUpdatedNodeContainer.setId(nodeContainer.getId());

        partialUpdatedNodeContainer.createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restNodeContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNodeContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNodeContainer))
            )
            .andExpect(status().isOk());

        // Validate the NodeContainer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeContainerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNodeContainer, nodeContainer),
            getPersistedNodeContainer(nodeContainer)
        );
    }

    @Test
    @Transactional
    void fullUpdateNodeContainerWithPatch() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the nodeContainer using partial update
        NodeContainer partialUpdatedNodeContainer = new NodeContainer();
        partialUpdatedNodeContainer.setId(nodeContainer.getId());

        partialUpdatedNodeContainer.statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restNodeContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNodeContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNodeContainer))
            )
            .andExpect(status().isOk());

        // Validate the NodeContainer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeContainerUpdatableFieldsEquals(partialUpdatedNodeContainer, getPersistedNodeContainer(partialUpdatedNodeContainer));
    }

    @Test
    @Transactional
    void patchNonExistingNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nodeContainerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNodeContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        nodeContainer.setId(UUID.randomUUID());

        // Create the NodeContainer
        NodeContainerDTO nodeContainerDTO = nodeContainerMapper.toDto(nodeContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeContainerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeContainerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NodeContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNodeContainer() throws Exception {
        // Initialize the database
        insertedNodeContainer = nodeContainerRepository.saveAndFlush(nodeContainer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the nodeContainer
        restNodeContainerMockMvc
            .perform(delete(ENTITY_API_URL_ID, nodeContainer.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return nodeContainerRepository.count();
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

    protected NodeContainer getPersistedNodeContainer(NodeContainer nodeContainer) {
        return nodeContainerRepository.findById(nodeContainer.getId()).orElseThrow();
    }

    protected void assertPersistedNodeContainerToMatchAllProperties(NodeContainer expectedNodeContainer) {
        assertNodeContainerAllPropertiesEquals(expectedNodeContainer, getPersistedNodeContainer(expectedNodeContainer));
    }

    protected void assertPersistedNodeContainerToMatchUpdatableProperties(NodeContainer expectedNodeContainer) {
        assertNodeContainerAllUpdatablePropertiesEquals(expectedNodeContainer, getPersistedNodeContainer(expectedNodeContainer));
    }
}
