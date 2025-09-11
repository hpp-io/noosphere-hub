package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.NodeAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.Node;
import io.hpp.noosphere.hub.repository.NodeRepository;
import io.hpp.noosphere.hub.repository.UserRepository;
import io.hpp.noosphere.hub.service.dto.NodeDTO;
import io.hpp.noosphere.hub.service.mapper.NodeMapper;
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
 * Integration tests for the {@link NodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_API_URL = "AAAAAAAAAA";
    private static final String UPDATED_API_URL = "BBBBBBBBBB";

    private static final String DEFAULT_API_KEY = "AAAAAAAAAA";
    private static final String UPDATED_API_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNodeMockMvc;

    private Node node;

    private Node insertedNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Node createEntity() {
        return new Node()
            .name(DEFAULT_NAME)
            .apiUrl(DEFAULT_API_URL)
            .apiKey(DEFAULT_API_KEY)
            .statusCode(DEFAULT_STATUS_CODE)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Node createUpdatedEntity() {
        return new Node()
            .name(UPDATED_NAME)
            .apiUrl(UPDATED_API_URL)
            .apiKey(UPDATED_API_KEY)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        node = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNode != null) {
            nodeRepository.delete(insertedNode);
            insertedNode = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);
        var returnedNodeDTO = om.readValue(
            restNodeMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NodeDTO.class
        );

        // Validate the Node in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNode = nodeMapper.toEntity(returnedNodeDTO);
        assertNodeUpdatableFieldsEquals(returnedNode, getPersistedNode(returnedNode));

        insertedNode = returnedNode;
    }

    @Test
    @Transactional
    void createNodeWithExistingId() throws Exception {
        // Create the Node with an existing ID
        insertedNode = nodeRepository.saveAndFlush(node);
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkApiUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        node.setApiUrl(null);

        // Create the Node, which fails.
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        restNodeMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApiKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        node.setApiKey(null);

        // Create the Node, which fails.
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        restNodeMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        node.setStatusCode(null);

        // Create the Node, which fails.
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        restNodeMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        node.setCreatedAt(null);

        // Create the Node, which fails.
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        restNodeMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNodes() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        // Get all the nodeList
        restNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].apiUrl").value(hasItem(DEFAULT_API_URL)))
            .andExpect(jsonPath("$.[*].apiKey").value(hasItem(DEFAULT_API_KEY)))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        // Get the node
        restNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, node.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(node.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.apiUrl").value(DEFAULT_API_URL))
            .andExpect(jsonPath("$.apiKey").value(DEFAULT_API_KEY))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNode() throws Exception {
        // Get the node
        restNodeMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node
        Node updatedNode = nodeRepository.findById(node.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNode are not directly saved in db
        em.detach(updatedNode);
        updatedNode
            .name(UPDATED_NAME)
            .apiUrl(UPDATED_API_URL)
            .apiKey(UPDATED_API_KEY)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        NodeDTO nodeDTO = nodeMapper.toDto(updatedNode);

        restNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNodeToMatchAllProperties(updatedNode);
    }

    @Test
    @Transactional
    void putNonExistingNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, nodeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node using partial update
        Node partialUpdatedNode = new Node();
        partialUpdatedNode.setId(node.getId());

        partialUpdatedNode.name(UPDATED_NAME).apiKey(UPDATED_API_KEY).statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT);

        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNode))
            )
            .andExpect(status().isOk());

        // Validate the Node in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedNode, node), getPersistedNode(node));
    }

    @Test
    @Transactional
    void fullUpdateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node using partial update
        Node partialUpdatedNode = new Node();
        partialUpdatedNode.setId(node.getId());

        partialUpdatedNode
            .name(UPDATED_NAME)
            .apiUrl(UPDATED_API_URL)
            .apiKey(UPDATED_API_KEY)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNode))
            )
            .andExpect(status().isOk());

        // Validate the Node in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeUpdatableFieldsEquals(partialUpdatedNode, getPersistedNode(partialUpdatedNode));
    }

    @Test
    @Transactional
    void patchNonExistingNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nodeDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(UUID.randomUUID());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the node
        restNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, node.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return nodeRepository.count();
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

    protected Node getPersistedNode(Node node) {
        return nodeRepository.findById(node.getId()).orElseThrow();
    }

    protected void assertPersistedNodeToMatchAllProperties(Node expectedNode) {
        assertNodeAllPropertiesEquals(expectedNode, getPersistedNode(expectedNode));
    }

    protected void assertPersistedNodeToMatchUpdatableProperties(Node expectedNode) {
        assertNodeAllUpdatablePropertiesEquals(expectedNode, getPersistedNode(expectedNode));
    }
}
