package io.hpp.noosphere.hub.service.blockchain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.web3j.protocol.core.Response;

public class CustomEthGetTransactionReceipt extends Response<CustomTransactionReceipt> {

  @Override
  @JsonDeserialize(as = CustomTransactionReceipt.class)
  public void setResult(CustomTransactionReceipt result) {
    super.setResult(result);
  }

  public Optional<CustomTransactionReceipt> getTransactionReceipt() {
    return Optional.ofNullable(getResult());
  }
}
