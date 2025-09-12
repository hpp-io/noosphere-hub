package io.hpp.noosphere.hub.repository;

import io.hpp.noosphere.hub.domain.Agent;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {}
