package io.hpp.noosphere.hub.service.blockchain;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.math.BigInteger;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

public class CustomTransactionReceipt extends TransactionReceipt {

  private BigInteger l1Fee;
  private BigInteger effectiveGasPrice;
  private BigInteger gasPrice;

  public BigInteger getL1Fee() {
    return l1Fee != null ? l1Fee : BigInteger.ZERO;
  }

  @JsonSetter("l1Fee")
  public void setL1Fee(String l1Fee) {
    if (l1Fee != null) {
      this.l1Fee = Numeric.decodeQuantity(l1Fee);
    }
  }

  @JsonSetter("effectiveGasPrice")
  public void setEffectiveGasPrice(String effectiveGasPrice) {
    if (effectiveGasPrice != null) {
      this.effectiveGasPrice = Numeric.decodeQuantity(effectiveGasPrice);
    }
  }

  @JsonSetter("gasPrice")
  public void setGasPrice(String gasPrice) {
    if (gasPrice != null) {
      this.gasPrice = Numeric.decodeQuantity(gasPrice);
    }
  }

  public BigInteger getL2Fee() {
    BigInteger gasUsed = getGasUsed();
    if (gasUsed == null) {
      return BigInteger.ZERO;
    }

    // EIP-1559 transactions
    if (effectiveGasPrice != null) {
      return gasUsed.multiply(effectiveGasPrice);
    }

    // Fallback for legacy transactions
    if (gasPrice != null) {
      return gasUsed.multiply(gasPrice);
    }

    return BigInteger.ZERO;
  }
}
