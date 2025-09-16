package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.AgentStatusAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.AgentStatus;
import io.hpp.noosphere.hub.repository.AgentStatusRepository;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import io.hpp.noosphere.hub.service.mapper.AgentStatusMapper;
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
 * Integration tests for the {@link AgentStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AgentStatusResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_KEEP_ALIVE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_KEEP_ALIVE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/agent-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgentStatusRepository agentStatusRepository;

    @Autowired
    private AgentStatusMapper agentStatusMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgentStatusMockMvc;

    private AgentStatus agentStatus;

    private AgentStatus insertedAgentStatus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentStatus createEntity() {
        return new AgentStatus().createdAt(DEFAULT_CREATED_AT).lastKeepAliveAt(DEFAULT_LAST_KEEP_ALIVE_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AgentStatus createUpdatedEntity() {
        return new AgentStatus().createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);
    }

    @BeforeEach
    void initTest() {
        agentStatus = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAgentStatus != null) {
            agentStatusRepository.delete(insertedAgentStatus);
            insertedAgentStatus = null;
        }
    }

    @Test
    @Transactional
    void createAgentStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);
        var returnedAgentStatusDTO = om.readValue(
            restAgentStatusMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentStatusDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentStatusDTO.class
        );

        // Validate the AgentStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgentStatus = agentStatusMapper.toEntity(returnedAgentStatusDTO);
        assertAgentStatusUpdatableFieldsEquals(returnedAgentStatus, getPersistedAgentStatus(returnedAgentStatus));

        insertedAgentStatus = returnedAgentStatus;
    }

    @Test
    @Transactional
    void createAgentStatusWithExistingId() throws Exception {
        // Create the AgentStatus with an existing ID
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentStatusMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agentStatus.setCreatedAt(null);

        // Create the AgentStatus, which fails.
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        restAgentStatusMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAgentStatuses() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        // Get all the agentStatusList
        restAgentStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agentStatus.getId().toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastKeepAliveAt").value(hasItem(DEFAULT_LAST_KEEP_ALIVE_AT.toString())));
    }

    @Test
    @Transactional
    void getAgentStatus() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        // Get the agentStatus
        restAgentStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, agentStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agentStatus.getId().toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.lastKeepAliveAt").value(DEFAULT_LAST_KEEP_ALIVE_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAgentStatus() throws Exception {
        // Get the agentStatus
        restAgentStatusMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgentStatus() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentStatus
        AgentStatus updatedAgentStatus = agentStatusRepository.findById(agentStatus.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgentStatus are not directly saved in db
        em.detach(updatedAgentStatus);
        updatedAgentStatus.createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(updatedAgentStatus);

        restAgentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentStatusDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isOk());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentStatusToMatchAllProperties(updatedAgentStatus);
    }

    @Test
    @Transactional
    void putNonExistingAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentStatusDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentStatusDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgentStatusWithPatch() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentStatus using partial update
        AgentStatus partialUpdatedAgentStatus = new AgentStatus();
        partialUpdatedAgentStatus.setId(agentStatus.getId());

        partialUpdatedAgentStatus.lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);

        restAgentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentStatus))
            )
            .andExpect(status().isOk());

        // Validate the AgentStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentStatusUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAgentStatus, agentStatus),
            getPersistedAgentStatus(agentStatus)
        );
    }

    @Test
    @Transactional
    void fullUpdateAgentStatusWithPatch() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agentStatus using partial update
        AgentStatus partialUpdatedAgentStatus = new AgentStatus();
        partialUpdatedAgentStatus.setId(agentStatus.getId());

        partialUpdatedAgentStatus.createdAt(UPDATED_CREATED_AT).lastKeepAliveAt(UPDATED_LAST_KEEP_ALIVE_AT);

        restAgentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgentStatus.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgentStatus))
            )
            .andExpect(status().isOk());

        // Validate the AgentStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentStatusUpdatableFieldsEquals(partialUpdatedAgentStatus, getPersistedAgentStatus(partialUpdatedAgentStatus));
    }

    @Test
    @Transactional
    void patchNonExistingAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentStatusDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgentStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agentStatus.setId(UUID.randomUUID());

        // Create the AgentStatus
        AgentStatusDTO agentStatusDTO = agentStatusMapper.toDto(agentStatus);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentStatusMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(agentStatusDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AgentStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgentStatus() throws Exception {
        // Initialize the database
        insertedAgentStatus = agentStatusRepository.saveAndFlush(agentStatus);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agentStatus
        restAgentStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, agentStatus.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return agentStatusRepository.count();
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

    protected AgentStatus getPersistedAgentStatus(AgentStatus agentStatus) {
        return agentStatusRepository.findById(agentStatus.getId()).orElseThrow();
    }

    protected void assertPersistedAgentStatusToMatchAllProperties(AgentStatus expectedAgentStatus) {
        assertAgentStatusAllPropertiesEquals(expectedAgentStatus, getPersistedAgentStatus(expectedAgentStatus));
    }

    protected void assertPersistedAgentStatusToMatchUpdatableProperties(AgentStatus expectedAgentStatus) {
        assertAgentStatusAllUpdatablePropertiesEquals(expectedAgentStatus, getPersistedAgentStatus(expectedAgentStatus));
    }
}
