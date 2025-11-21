package io.hpp.noosphere.hub.service.blockchain;

import java.util.concurrent.ScheduledExecutorService;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;

public class CustomWeb3jService extends JsonRpc2_0Web3j {

  private final Web3jService web3jService;

  public CustomWeb3jService(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
    super(web3jService, pollingInterval, scheduledExecutorService);
    this.web3jService = web3jService;
  }

  public Request<?, CustomEthGetTransactionReceipt> getCustomTransactionReceipt(String transactionHash) {
    return new Request<>(
      "eth_getTransactionReceipt",
      java.util.Arrays.asList(transactionHash),
      web3jService,
      CustomEthGetTransactionReceipt.class
    );
  }
}
