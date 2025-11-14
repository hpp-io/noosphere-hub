package io.hpp.noosphere.hub.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import io.hpp.noosphere.hub.config.OpenApiConfiguration;
import io.hpp.noosphere.hub.domain.Verifier;
import io.hpp.noosphere.hub.exception.PermissionDeniedException;
import io.hpp.noosphere.hub.exception.VerifierNotFoundException;
import io.hpp.noosphere.hub.repository.VerifierRepository;
import io.hpp.noosphere.hub.service.UserService;
import io.hpp.noosphere.hub.service.VerifierService;
import io.hpp.noosphere.hub.service.dto.JsonViewType;
import io.hpp.noosphere.hub.service.dto.VerifierDTO;
import io.hpp.noosphere.hub.web.rest.errors.BadRequestAlertException;
import io.hpp.noosphere.hub.web.rest.vm.SearchVerifierVm;
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
 * REST controller for managing {@link Verifier}.
 */
@RestController
@RequestMapping("/api/verifiers")
@Tag(
  name = "Verifier",
  description = "Verifier Controller",
  extensions = { @Extension(properties = { @ExtensionProperty(name = OpenApiConfiguration.TAG_ORDER, value = "1") }) }
)
public class VerifierResource {

  private static final Logger LOG = LoggerFactory.getLogger(VerifierResource.class);

  private static final String ENTITY_NAME = "nooSphereHubVerifier";
  private final IAuthenticationFacade authenticationFacade;
  private final VerifierService verifierService;
  private final VerifierRepository verifierRepository;
  private final UserService userService;

  @Value("${jhipster.clientApp.name}")
  private String applicationName;

  public VerifierResource(
    VerifierService verifierService,
    VerifierRepository verifierRepository,
    UserService userService,
    IAuthenticationFacade authenticationFacade
  ) {
    this.authenticationFacade = authenticationFacade;
    this.verifierService = verifierService;
    this.verifierRepository = verifierRepository;
    this.userService = userService;
  }

  @PostMapping("")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<VerifierDTO> createVerifier(@RequestBody VerifierDTO verifierDTO) throws URISyntaxException {
    LOG.debug("REST request to save Verifier : {}", verifierDTO);
    if (verifierDTO.getId() != null) {
      throw new BadRequestAlertException("A new verifier cannot already have an ID", ENTITY_NAME, "idexists");
    }
    Instant now = Instant.now();
    verifierDTO = verifierService.create(authenticationFacade.getUserId(), verifierDTO, now);
    return ResponseEntity.created(new URI("/api/verifiers/" + verifierDTO.getId()))
      .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, verifierDTO.getId().toString()))
      .body(verifierDTO);
  }

  /**
   * {@code PUT  /verifiers/:id} : Updates an existing verifier.
   *
   * @param id           the id of the verifierDTO to save.
   * @param verifierDTO the verifierDTO to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated verifierDTO, or with status {@code 400 (Bad Request)} if the
   * verifierDTO is not valid, or with status {@code 500 (Internal Server Error)} if the verifierDTO couldn't be updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<VerifierDTO> updateVerifier(
    @PathVariable(value = "id", required = true) final UUID id,
    @Valid @RequestBody VerifierDTO verifierDTO
  ) throws PermissionDeniedException, VerifierNotFoundException {
    LOG.debug("REST request to update Verifier : {}, {}", id, verifierDTO);
    if (verifierDTO.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, verifierDTO.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    }

    if (!verifierRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    }
    Instant now = Instant.now();
    verifierDTO = verifierService.partialUpdate(userService, authenticationFacade.getUserId(), verifierDTO, now);
    return ResponseEntity.ok()
      .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, verifierDTO.getId().toString()))
      .body(verifierDTO);
  }

  /**
   * {@code POST  /verifiers/search} : search verifiers.
   *
   * @param searchVm the search criteria of the request.
   * @param pageable the pagination information.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of verifiers in body.
   */
  @PostMapping("/search")
  @Operation(summary = "Search Verifier")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    required = true,
    description = "Search Criteria",
    content = @Content(schema = @Schema(implementation = SearchVerifierVm.class), mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE)
  )
  @ApiResponses(
    {
      @ApiResponse(
        responseCode = "200",
        content = @Content(
          mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = VerifierDTO.class))
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
  public ResponseEntity<List<VerifierDTO>> search(
    @RequestBody SearchVerifierVm searchVm,
    @org.springdoc.core.annotations.ParameterObject Pageable pageable
  ) {
    LOG.debug("REST request to search Verifiers");
    Page<VerifierDTO> page = verifierService.search(
      searchVm.getSearchText(),
      searchVm.getName(),
      searchVm.getStatusCode(),
      searchVm.getCreatedByUserId(),
      searchVm.getWalletAddress(),
      searchVm.getVerifierAddress(),
      pageable
    );
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    return ResponseEntity.ok().headers(headers).body(page.getContent());
  }

  /**
   * {@code GET  /verifiers/:id} : get the "id" verifier.
   *
   * @param id the id of the verifierDTO to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the verifierDTO, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/{id}")
  @JsonView(JsonViewType.Update.class)
  public ResponseEntity<VerifierDTO> getVerifier(@PathVariable("id") UUID id) {
    LOG.debug("REST request to get Verifier : {}", id);
    Optional<VerifierDTO> verifierDTO = verifierService.findOne(id);
    return ResponseUtil.wrapOrNotFound(verifierDTO);
  }

  /**
   * {@code DELETE  /verifiers/:id} : delete the "id" verifier.
   *
   * @param id the id of the verifierDTO to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVerifier(@PathVariable("id") UUID id) throws PermissionDeniedException, VerifierNotFoundException {
    LOG.debug("REST request to delete Verifier : {}", id);
    verifierService.delete(authenticationFacade.getUserId(), id);
    return ResponseEntity.noContent()
      .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
      .build();
  }
}
