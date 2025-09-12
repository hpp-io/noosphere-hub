package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.AgentContainerAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.AgentContainer;
import io.hpp.noosphere.hub.repository.AgentContainerRepository;
import io.hpp.noosphere.hub.service.dto.AgentContainerDTO;
import io.hpp.noosphere.hub.service.mapper.AgentContainerMapper;
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
 * Integration tests for the {@link AgentContainerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgentContainerResourceIT {

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/agent-containers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgentContainerRepository agentContainerRepository;

    @Autowired
    private AgentContainerMapper agentContainerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgentContainerMockMvc;

    private AgentContainer agentContainer;

    private AgentContainer insertedAgentContainer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentContainer createEntity() {
        return new AgentContainer().statusCode(DEFAULT_STATUS_CODE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentContainer createUpdatedEntity() {
        return new AgentContainer().statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        agentContainer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAgentContainer != null) {
            agentContainerRepository.delete(insertedAgentContainer);
            insertedAgentContainer = null;
        }
    }

    @Test
    @Transactional
    void createAgentContainer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);
        var returnedAgentContainerDTO = om.readValue(
            restAgentContainerMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(agentContainerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentContainerDTO.class
        );

        // Validate the AgentContainer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgentContainer = agentContainerMapper.toEntity(returnedAgentContainerDTO);
        assertAgentContainerUpdatableFieldsEquals(returnedAgentContainer, getPersistedAgentContainer(returnedAgentContainer));

        insertedAgentContainer = returnedAgentContainer;
    }

    @Test
    @Transactional
    void createAgentContainerWithExistingId() throws Exception {
        // Create the AgentContainer with an existing ID
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agentContainer.setStatusCode(null);

        // Create the AgentContainer, which fails.
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        restAgentContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agentContainer.setCreatedAt(null);

        // Create the AgentContainer, which fails.
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        restAgentContainerMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAgentContainers() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        // Get all the agentContainerList
        restAgentContainerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentContainer.getId().toString())))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAgentContainer() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        // Get the agentContainer
        restAgentContainerMockMvc
            .perform(get(ENTITY_API_URL_ID, agentContainer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agentContainer.getId().toString()))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAgentContainer() throws Exception {
        // Get the agentContainer
        restAgentContainerMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgentContainer() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentContainer
        AgentContainer updatedAgentContainer = agentContainerRepository.findById(agentContainer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgentContainer are not directly saved in db
        em.detach(updatedAgentContainer);
        updatedAgentContainer.statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(updatedAgentContainer);

        restAgentContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentContainerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isOk());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentContainerToMatchAllProperties(updatedAgentContainer);
    }

    @Test
    @Transactional
    void putNonExistingAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentContainerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgentContainerWithPatch() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentContainer using partial update
        AgentContainer partialUpdatedAgentContainer = new AgentContainer();
        partialUpdatedAgentContainer.setId(agentContainer.getId());

        restAgentContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentContainer))
            )
            .andExpect(status().isOk());

        // Validate the AgentContainer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentContainerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAgentContainer, agentContainer),
            getPersistedAgentContainer(agentContainer)
        );
    }

    @Test
    @Transactional
    void fullUpdateAgentContainerWithPatch() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentContainer using partial update
        AgentContainer partialUpdatedAgentContainer = new AgentContainer();
        partialUpdatedAgentContainer.setId(agentContainer.getId());

        partialUpdatedAgentContainer.statusCode(UPDATED_STATUS_CODE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restAgentContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentContainer))
            )
            .andExpect(status().isOk());

        // Validate the AgentContainer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentContainerUpdatableFieldsEquals(partialUpdatedAgentContainer, getPersistedAgentContainer(partialUpdatedAgentContainer));
    }

    @Test
    @Transactional
    void patchNonExistingAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentContainerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgentContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentContainer.setId(UUID.randomUUID());

        // Create the AgentContainer
        AgentContainerDTO agentContainerDTO = agentContainerMapper.toDto(agentContainer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentContainerMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentContainerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentContainer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgentContainer() throws Exception {
        // Initialize the database
        insertedAgentContainer = agentContainerRepository.saveAndFlush(agentContainer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agentContainer
        restAgentContainerMockMvc
            .perform(delete(ENTITY_API_URL_ID, agentContainer.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return agentContainerRepository.count();
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

    protected AgentContainer getPersistedAgentContainer(AgentContainer agentContainer) {
        return agentContainerRepository.findById(agentContainer.getId()).orElseThrow();
    }

    protected void assertPersistedAgentContainerToMatchAllProperties(AgentContainer expectedAgentContainer) {
        assertAgentContainerAllPropertiesEquals(expectedAgentContainer, getPersistedAgentContainer(expectedAgentContainer));
    }

    protected void assertPersistedAgentContainerToMatchUpdatableProperties(AgentContainer expectedAgentContainer) {
        assertAgentContainerAllUpdatablePropertiesEquals(expectedAgentContainer, getPersistedAgentContainer(expectedAgentContainer));
    }
}
