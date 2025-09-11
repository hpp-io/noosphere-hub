package io.hpp.noosphere.hub.repository;

import io.hpp.noosphere.hub.domain.NodeContainer;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NodeContainer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NodeContainerRepository extends JpaRepository<NodeContainer, UUID> {}
