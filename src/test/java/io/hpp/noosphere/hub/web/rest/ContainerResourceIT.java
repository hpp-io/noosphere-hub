package io.hpp.noosphere.hub.web.rest;

import static io.hpp.noosphere.hub.domain.ContainerAsserts.*;
import static io.hpp.noosphere.hub.web.rest.TestUtil.createUpdateProxyForBean;
import static io.hpp.noosphere.hub.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hpp.noosphere.hub.IntegrationTest;
import io.hpp.noosphere.hub.domain.Container;
import io.hpp.noosphere.hub.repository.ContainerRepository;
import io.hpp.noosphere.hub.repository.UserRepository;
import io.hpp.noosphere.hub.service.dto.ContainerDTO;
import io.hpp.noosphere.hub.service.mapper.ContainerMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ContainerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContainerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WALLET_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_WALLET_ADDRESS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final String DEFAULT_STATUS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STATUS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PARAMETERS = "AAAAAAAAAA";
    private static final String UPDATED_PARAMETERS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/containers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContainerMapper containerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContainerMockMvc;

    private Container container;

    private Container insertedContainer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Container createEntity() {
        return new Container()
            .name(DEFAULT_NAME)
            .walletAddress(DEFAULT_WALLET_ADDRESS)
            .price(DEFAULT_PRICE)
            .statusCode(DEFAULT_STATUS_CODE)
            .description(DEFAULT_DESCRIPTION)
            .parameters(DEFAULT_PARAMETERS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Container createUpdatedEntity() {
        return new Container()
            .name(UPDATED_NAME)
            .walletAddress(UPDATED_WALLET_ADDRESS)
            .price(UPDATED_PRICE)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .parameters(UPDATED_PARAMETERS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        container = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContainer != null) {
            containerRepository.delete(insertedContainer);
            insertedContainer = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createContainer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);
        var returnedContainerDTO = om.readValue(
            restContainerMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContainerDTO.class
        );

        // Validate the Container in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContainer = containerMapper.toEntity(returnedContainerDTO);
        assertContainerUpdatableFieldsEquals(returnedContainer, getPersistedContainer(returnedContainer));

        insertedContainer = returnedContainer;
    }

    @Test
    @Transactional
    void createContainerWithExistingId() throws Exception {
        // Create the Container with an existing ID
        insertedContainer = containerRepository.saveAndFlush(container);
        ContainerDTO containerDTO = containerMapper.toDto(container);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContainerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkWalletAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        container.setWalletAddress(null);

        // Create the Container, which fails.
        ContainerDTO containerDTO = containerMapper.toDto(container);

        restContainerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        container.setPrice(null);

        // Create the Container, which fails.
        ContainerDTO containerDTO = containerMapper.toDto(container);

        restContainerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        container.setStatusCode(null);

        // Create the Container, which fails.
        ContainerDTO containerDTO = containerMapper.toDto(container);

        restContainerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        container.setCreatedAt(null);

        // Create the Container, which fails.
        ContainerDTO containerDTO = containerMapper.toDto(container);

        restContainerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContainers() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        // Get all the containerList
        restContainerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(container.getId().toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].walletAddress").value(hasItem(DEFAULT_WALLET_ADDRESS)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].parameters").value(hasItem(DEFAULT_PARAMETERS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getContainer() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        // Get the container
        restContainerMockMvc
            .perform(get(ENTITY_API_URL_ID, container.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(container.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.walletAddress").value(DEFAULT_WALLET_ADDRESS))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.parameters").value(DEFAULT_PARAMETERS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingContainer() throws Exception {
        // Get the container
        restContainerMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContainer() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the container
        Container updatedContainer = containerRepository.findById(container.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContainer are not directly saved in db
        em.detach(updatedContainer);
        updatedContainer
            .name(UPDATED_NAME)
            .walletAddress(UPDATED_WALLET_ADDRESS)
            .price(UPDATED_PRICE)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .parameters(UPDATED_PARAMETERS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ContainerDTO containerDTO = containerMapper.toDto(updatedContainer);

        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, containerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContainerToMatchAllProperties(updatedContainer);
    }

    @Test
    @Transactional
    void putNonExistingContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, containerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(containerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContainerWithPatch() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the container using partial update
        Container partialUpdatedContainer = new Container();
        partialUpdatedContainer.setId(container.getId());

        partialUpdatedContainer
            .name(UPDATED_NAME)
            .walletAddress(UPDATED_WALLET_ADDRESS)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContainer))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContainerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContainer, container),
            getPersistedContainer(container)
        );
    }

    @Test
    @Transactional
    void fullUpdateContainerWithPatch() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the container using partial update
        Container partialUpdatedContainer = new Container();
        partialUpdatedContainer.setId(container.getId());

        partialUpdatedContainer
            .name(UPDATED_NAME)
            .walletAddress(UPDATED_WALLET_ADDRESS)
            .price(UPDATED_PRICE)
            .statusCode(UPDATED_STATUS_CODE)
            .description(UPDATED_DESCRIPTION)
            .parameters(UPDATED_PARAMETERS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContainer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContainer))
            )
            .andExpect(status().isOk());

        // Validate the Container in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContainerUpdatableFieldsEquals(partialUpdatedContainer, getPersistedContainer(partialUpdatedContainer));
    }

    @Test
    @Transactional
    void patchNonExistingContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, containerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContainer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        container.setId(UUID.randomUUID());

        // Create the Container
        ContainerDTO containerDTO = containerMapper.toDto(container);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContainerMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(containerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Container in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContainer() throws Exception {
        // Initialize the database
        insertedContainer = containerRepository.saveAndFlush(container);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the container
        restContainerMockMvc
            .perform(delete(ENTITY_API_URL_ID, container.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return containerRepository.count();
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

    protected Container getPersistedContainer(Container container) {
        return containerRepository.findById(container.getId()).orElseThrow();
    }

    protected void assertPersistedContainerToMatchAllProperties(Container expectedContainer) {
        assertContainerAllPropertiesEquals(expectedContainer, getPersistedContainer(expectedContainer));
    }

    protected void assertPersistedContainerToMatchUpdatableProperties(Container expectedContainer) {
        assertContainerAllUpdatablePropertiesEquals(expectedContainer, getPersistedContainer(expectedContainer));
    }
}
