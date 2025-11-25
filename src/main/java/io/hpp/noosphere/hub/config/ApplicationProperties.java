package io.hpp.noosphere.hub.config;

import io.hpp.noosphere.common.config.SharedApplicationProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties extends SharedApplicationProperties {

  private final Liquibase liquibase = new Liquibase();
  private final ScheduleTask scheduleTask = new ScheduleTask();

  // jhipster-needle-application-properties-property

  // jhipster-needle-application-properties-property-getter

  @Data
  public static class Liquibase {

    private Boolean asyncStart = true;
  }

  // jhipster-needle-application-properties-property-class

  @Data
  public static class CronConfig {

    private Boolean enableCron;
    private String cron;
  }

  @Data
  public static class AgentStatusConfig extends CronConfig {

    private Long unhealthyTimeout;
  }

  @Data
  public static class ScheduleTask {

    private AgentStatusConfig agentStatus = new AgentStatusConfig();
  }
}
