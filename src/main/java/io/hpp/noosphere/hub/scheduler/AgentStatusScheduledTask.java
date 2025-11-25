package io.hpp.noosphere.hub.scheduler;

import static io.hpp.noosphere.hub.config.Constants.COLUMN_NAME_LAST_KEEP_ALIVE_AT;

import io.hpp.noosphere.common.domain.enumeration.StatusCode;
import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.service.AgentService;
import io.hpp.noosphere.hub.service.AgentStatusService;
import io.hpp.noosphere.hub.service.dto.AgentStatusDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AgentStatusScheduledTask {

  private static final Logger LOG = LoggerFactory.getLogger(AgentStatusScheduledTask.class);

  private final AgentStatusService agentStatusService;
  private final AgentService agentService;
  private final ApplicationProperties applicationProperties;

  public AgentStatusScheduledTask(
    AgentStatusService agentStatusService,
    AgentService agentService,
    ApplicationProperties applicationProperties
  ) {
    this.agentStatusService = agentStatusService;
    this.agentService = agentService;
    this.applicationProperties = applicationProperties;
  }

  @Scheduled(cron = "${application.schedule-task.agent-status.cron}")
  public void validateAgentStatus() {
    try {
      if (Boolean.TRUE.equals(applicationProperties.getScheduleTask().getAgentStatus().getEnableCron())) {
        LOG.debug("validateAgentStatus scheduled task enabled");
        boolean allProcessed = false;
        int loopCount = 0;
        Instant now = Instant.now();
        Instant compareTime = now.minus(applicationProperties.getScheduleTask().getAgentStatus().getUnhealthyTimeout(), ChronoUnit.MINUTES);
        try {
          while (!allProcessed) {
            Pageable pageable = PageRequest.of(loopCount, 100, Sort.by(Order.desc(COLUMN_NAME_LAST_KEEP_ALIVE_AT)));
            Page<AgentStatusDTO> entityList = agentStatusService.search(null, StatusCode.ACTIVE, pageable);
            if (entityList.isEmpty()) {
              allProcessed = true;
            } else {
              for (AgentStatusDTO oneDTO : entityList.getContent()) {
                if (oneDTO.getLastKeepAliveAt() != null) {
                  if (compareTime.isAfter(oneDTO.getLastKeepAliveAt())) {
                    agentService.updateStatus(oneDTO.getAgent().getId(), StatusCode.INACTIVE, now);
                  }
                }
              }
            }
            loopCount++;
          }
        } catch (Exception e2) {
          LOG.error("Failed to process validateAgentStatus", e2);
        }
      }
    } catch (Exception e) {
      LOG.error("Failed to process validateAgentStatus", e);
    }
  }
}
