package io.hpp.noosphere.hub.service.blockchain;

import static io.hpp.noosphere.hub.config.Constants.EMPTY_ADDRESS;

import io.hpp.noosphere.hub.config.Web3jConfig;
import io.hpp.noosphere.hub.service.blockchain.contract.WalletFactory;
import io.hpp.noosphere.hub.service.blockchain.contract.WalletFactory.WalletCreatedEventResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class Web3WalletFactoryService {

  private static final Logger log = LoggerFactory.getLogger(Web3WalletFactoryService.class);

  private final Web3j web3j;
  private final Web3RouterService web3RouterService;
  private final Credentials credentials;
  private final Web3jConfig.CustomGasProvider gasProvider;

  private WalletFactory walletFactoryContract;

  public Web3WalletFactoryService(
    Web3j web3j,
    Web3RouterService web3RouterService,
    Credentials credentials,
    Web3jConfig.CustomGasProvider gasProvider
  ) {
    this.web3j = web3j;
    this.web3RouterService = web3RouterService;
    this.credentials = credentials;
    this.gasProvider = gasProvider;
  }

  @PostConstruct
  public void init() {
    try {
      String contractAddress = web3RouterService.getSyncWalletFactoryAddress();

      if (contractAddress == null || contractAddress.isEmpty() || contractAddress.equals(EMPTY_ADDRESS)) {
        throw new IllegalStateException("WalletFactory contract address not found via Router.");
      }

      this.walletFactoryContract = WalletFactory.load(contractAddress, web3j, credentials, gasProvider);

      log.info("Successfully initialized WalletFactoryService with contract at address: {}", contractAddress);
    } catch (Exception e) {
      log.error("Failed to initialize WalletFactoryService", e);
      throw new RuntimeException("Could not initialize WalletFactoryService", e);
    }
  }

  public CompletableFuture<TransactionReceipt> createWallet(String initialOwner) {
    if (walletFactoryContract == null) {
      log.error("WalletFactory contract is not loaded.");
      return CompletableFuture.failedFuture(new IllegalStateException("WalletFactory contract not initialized."));
    }

    return walletFactoryContract.createWallet(initialOwner).sendAsync();
  }

  public Boolean isValid(String walletAddress) throws Exception {
    if (walletFactoryContract == null) {
      log.error("WalletFactory contract is not loaded.");
      throw new IllegalStateException("WalletFactory contract not initialized.");
    }

    try {
      return walletFactoryContract.isValidWallet(walletAddress).send();
    } catch (Exception e) {
      log.error("Failed to is valid wallet: {}", walletAddress, e);
      throw new RuntimeException("Failed to is valid wallet", e);
    }
  }

  public String getWalletAddressFromTransactionReceipt(TransactionReceipt transactionReceipt) {
    if (transactionReceipt != null) {
      List<WalletCreatedEventResponse> list = WalletFactory.getWalletCreatedEvents(transactionReceipt);
      if (!list.isEmpty()) {
        for (WalletCreatedEventResponse walletCreatedEventResponse : list) {
          WalletCreatedEventResponse createdEvent = WalletFactory.getWalletCreatedEventFromLog(walletCreatedEventResponse.log);
          return createdEvent.walletAddress;
        }
      }
    }
    return null;
  }
}
