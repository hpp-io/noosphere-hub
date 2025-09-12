package io.hpp.noosphere.hub.repository;

import io.hpp.noosphere.hub.domain.AgentStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AgentStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AgentStatusRepository extends JpaRepository<AgentStatus, UUID> {}
