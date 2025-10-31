package io.hpp.noosphere.hub.service.blockchain;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class WalletService {

  private static final Logger log = LoggerFactory.getLogger(WalletService.class);

  private final ApplicationProperties.Blockchain blockchainConfig;
  private final Web3j web3j;
  private final Web3WalletFactoryService web3WalletFactoryService;

  public WalletService(
    ApplicationProperties applicationProperties,
    Web3WalletFactoryService web3WalletFactoryService,
    Web3j web3j
  ) {
    this.blockchainConfig = applicationProperties.getBlockchain();
    this.web3WalletFactoryService = web3WalletFactoryService;
    this.web3j = web3j;
  }

  private String extractWalletAddressFromReceipt(TransactionReceipt transactionReceipt) {
    if (transactionReceipt == null || transactionReceipt.getLogs() == null || transactionReceipt.getLogs().isEmpty()) {
      return null;
    }
    return web3WalletFactoryService.getWalletAddressFromTransactionReceipt(transactionReceipt);
  }

  public String createAndUpdateWallet(String ownerAddress) {
    try {
      CompletableFuture<String> walletAddressFuture = web3WalletFactoryService.createWallet(ownerAddress)
        .thenApply(transactionReceipt -> {

          String walletAddress = extractWalletAddressFromReceipt(transactionReceipt);
          if (!CommonUtils.isValid(walletAddress)) {
            throw new IllegalStateException("Failed to extract wallet address from receipt for owner: " + ownerAddress);
          }
          return walletAddress;

        });
      return walletAddressFuture.join();

    } catch (Exception ex) {
      log.error("Error creating wallet for owner : {}", ownerAddress, ex);
      throw new RuntimeException("Failed to create wallet for owner : " + ownerAddress, ex);
    }
  }
}
