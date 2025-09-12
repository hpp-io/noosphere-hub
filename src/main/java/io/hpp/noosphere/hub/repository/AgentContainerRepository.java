package io.hpp.noosphere.hub.repository;

import io.hpp.noosphere.hub.domain.AgentContainer;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AgentContainer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentContainerRepository extends JpaRepository<AgentContainer, UUID> {}
