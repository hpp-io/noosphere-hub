package io.hpp.noosphere.hub.service.blockchain.dto;


import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;


@Data
public class SubscriptionDTO implements Serializable {


  private Long id;

  private String routeId;

  private String containerId;

  private BigInteger feeAmount = BigInteger.ZERO;

  private String client;

  private Long activeAt;

  private Long intervalSeconds = 0L;

  private Long maxExecutions = 1L;

  private String wallet;

  private String feeToken;

  private String verifier;

  private Integer redundancy = 1;

  private Boolean useDeliveryInbox = false;


}

