package io.hpp.noosphere.hub.repository;

import io.hpp.noosphere.hub.domain.NodeStatus;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NodeStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NodeStatusRepository extends JpaRepository<NodeStatus, UUID> {}
