package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.repository.ValidatorRepository;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.ValidatorService;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.service.dto.ValidatorDTO;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.RegisterValidatorVm;
import io.hpp.noosphere.hub.web.rest.vm.SearchValidatorVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.hpp.noosphere.hub.domain.Validator}.
 */
@RestController
@RequestMapping("/api/validators")
@Tag(
  name = "Validator",
  description = "Validator Controller",
  extensions = {@Extension(properties = {@ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "1")})}
)
public class ValidatorResource {

  private static final Logger LOG = LoggerFactory.getLogger(ValidatorResource.class);

  private static final String ENTITY_NAME = "nooSphereHubValidator";
  private final IAuthenticationFacade authenticationFacade;
  private final ValidatorService validatorService;
  private final ValidatorRepository validatorRepository;
  private final UserService userService;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public ValidatorResource(
    ValidatorService validatorService,
    ValidatorRepository validatorRepository,
    UserService userService,
    IAuthenticationFacade authenticationFacade
  ) {
    this.authenticationFacade = authenticationFacade;
    this.validatorService = validatorService;
    this.validatorRepository = validatorRepository;
    this.userService = userService;
  }

  /**
   * {@code PUT  /validators/:id} : Updates an existing validator.
   *
   * @param id           the id of the validatorDTO to save.
   * @param validatorDTO the validatorDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validatorDTO, or with status {@code 400 (Bad Request)} if the
   * validatorDTO is not valid, or with status {@code 500 (Internal Server Error)} if the validatorDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<ValidatorDTO> updateValidator(
    @PathVariable(value = "id", required = true) final UUID id,
    @Valid @RequestBody ValidatorDTO validatorDTO
  ) throws PermissionDeniedException {
    LOG.debug("REST request to update Validator : {}, {}", id, validatorDTO);
    if (validatorDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, validatorDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!validatorRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }
    Instant now = Instant.now();
    validatorDTO = validatorService.partialUpdate(userService, authenticationFacade.getUserId(), validatorDTO, now);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, validatorDTO.getId().toString()))
      .body(validatorDTO);
  }

  /**
   * {@code POST  /validators/search} : search validators.
   *
   * @param searchVm the search criteria of the request.
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of validators in body.
   */
  @PostMapping("/search")
  @Operation(summary = "Search Validator")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    required = true,
    description = "Search Criteria",
    content = @Content(schema = @Schema(implementation = SearchValidatorVm.class), mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE)
  )
  @ApiResponses(
    {
      @ApiResponse(
        responseCode = "200",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = ValidatorDTO.class))
        ),
        description = "Successful operation"
      ),
      @ApiResponse(
        responseCode = "500",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE),
        description = "Internal server error"
      ),
    }
  )
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<List<ValidatorDTO>> search(
    @RequestBody SearchValidatorVm searchVm,
    @org.springdoc.core.annotations.ParameterObject Pageable pageable
  ) {
    LOG.debug("REST request to search Validators");
    Page<ValidatorDTO> page = validatorService.search(
      searchVm.getSearchText(),
      searchVm.getName(),
      searchVm.getStatusCode(),
      searchVm.getCreatedByUserId(),
      searchVm.getWalletAddress(),
      searchVm.getVerifierAddress(),
      pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /validators/:id} : get the "id" validator.
   *
   * @param id the id of the validatorDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validatorDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<ValidatorDTO> getValidator(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get Validator : {}", id);
    Optional<ValidatorDTO> validatorDTO = validatorService.findOne(id);
    return ResponseUtil.wrapOrNotFound(validatorDTO);
  }

  /**
   * {@code DELETE  /validators/:id} : delete the "id" validator.
   *
   * @param id the id of the validatorDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteValidator(@PathVariable("id") UUID id) throws PermissionDeniedException {
    LOG.debug("REST request to delete Validator : {}", id);
    validatorService.delete(authenticationFacade.getUserId(), id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }

  @JsonView(JsonViewType.Shallow.class)
  @PostMapping("/register")
  public ResponseEntity<ValidatorDTO> registerValidator(@Valid @RequestBody RegisterValidatorVm validatorVm) throws URISyntaxException {
    LOG.debug("REST request to register Validator : {}", validatorVm);
    Instant now = Instant.now();
    ValidatorDTO validatorDTO = validatorService.register(validatorVm.getName(), validatorVm.getApiKey(), validatorVm.getWalletAddress(),
      validatorVm.getVerifierAddress(), validatorVm.getEmail(), now);
    return ResponseEntity.created(new URI("/api/validators/" + validatorDTO.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, validatorDTO.getId().toString()))
      .body(validatorDTO);
  }
}
