package io.hpp.noosphere.hub.service.blockchain;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.service.blockchain.contract.Wallet;
import io.hpp.noosphere.hub.service.blockchain.contract.WalletFactory;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class WalletService {

  private static final Logger log = LoggerFactory.getLogger(WalletService.class);

  private final ApplicationProperties.Blockchain blockchainConfig;
  private final CustomWeb3jService web3j;
  private final Web3WalletFactoryService web3WalletFactoryService;
  private final Map<String, Event> eventRegistry;

  public record TransactionFees(BigInteger l1Fee, BigInteger l2Fee) {}

  public WalletService(
    ApplicationProperties applicationProperties,
    Web3WalletFactoryService web3WalletFactoryService,
    CustomWeb3jService web3j
  ) {
    this.blockchainConfig = applicationProperties.getBlockchain();
    this.web3WalletFactoryService = web3WalletFactoryService;
    this.web3j = web3j;
    this.eventRegistry = new ConcurrentHashMap<>();
    registerEvent(WalletFactory.WALLETCREATED_EVENT);
    registerEvent(Wallet.APPROVAL_EVENT);
    registerEvent(Wallet.DEPOSIT_EVENT);
    registerEvent(Wallet.ESCROW_EVENT);
    registerEvent(Wallet.OWNERSHIPTRANSFERRED_EVENT);
    registerEvent(Wallet.REQUESTDISBURSED_EVENT);
    registerEvent(Wallet.REQUESTLOCKED_EVENT);
    registerEvent(Wallet.REQUESTRELEASED_EVENT);
    registerEvent(Wallet.TRANSFER_EVENT);
    registerEvent(Wallet.WITHDRAW_EVENT);
  }

  public void registerEvent(Event event) {
    log.info("Registering event type: {}", event.getName());
    eventRegistry.put(event.getName(), event);
  }

  private String extractWalletAddressFromReceipt(TransactionReceipt transactionReceipt) {
    if (transactionReceipt == null || transactionReceipt.getLogs() == null || transactionReceipt.getLogs().isEmpty()) {
      return null;
    }
    return web3WalletFactoryService.getWalletAddressFromTransactionReceipt(transactionReceipt);
  }

  public String createWallet(String ownerAddress) {
    try {
      CompletableFuture<String> walletAddressFuture = web3WalletFactoryService
        .createWallet(ownerAddress)
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

  public Transaction getTransactionByRequestId(String requestId) {
    try {
      return web3j.ethGetTransactionByHash(requestId).send().getTransaction().orElse(null);
    } catch (IOException e) {
      log.error("Error looking up transaction for request id : {}", requestId, e);
      throw new RuntimeException(e);
    }
  }

  public List<EthLog.LogResult> getEventsByWalletAndRequestId(String walletAddress, String requestId) {
    try {
      EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, walletAddress);
      return web3j
        .ethGetLogs(filter)
        .send()
        .getLogs()
        .stream()
        .filter(logResult -> {
          // Assuming the requestId is stored in one of the topics
          return ((EthLog.LogObject) logResult.get()).getTopics().stream().anyMatch(topic -> topic.contains(requestId));
        })
        .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("Error looking up event logs for wallet address : {} and request id : {}", walletAddress, requestId, e);
      return Collections.emptyList();
    }
  }

  public List<EthLog.LogResult> getEventsByWallet(String walletAddress) {
    try {
      EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, walletAddress);
      return web3j.ethGetLogs(filter).send().getLogs();
    } catch (IOException e) {
      log.error("Error looking up event logs for wallet address : {}", walletAddress, e);
      return Collections.emptyList();
    }
  }

  public List<EthLog.LogResult> getEventsByWalletAndType(String walletAddress, String eventType) {
    Event event = eventRegistry.get(eventType);
    if (event == null) {
      log.warn("Event type '{}' is not registered. Use registerEvent() to add it.", eventType);
      return Collections.emptyList();
    }
    return getEventsByWalletAndEvent(walletAddress, event);
  }

  public List<EthLog.LogResult> getEventsByWalletAndEvent(String walletAddress, Event event) {
    try {
      EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, walletAddress);
      filter.addSingleTopic(EventEncoder.encode(event));
      return web3j.ethGetLogs(filter).send().getLogs();
    } catch (IOException e) {
      log.error("Error looking up event logs for wallet address : {} and event : {}", walletAddress, event.getName(), e);
      return Collections.emptyList();
    }
  }

  public TransactionFees getTransactionFeesFromLog(EthLog.LogResult logResult) {
    if (logResult == null || !(logResult.get() instanceof EthLog.LogObject)) {
      log.warn("Invalid log result provided.");
      return new TransactionFees(BigInteger.ZERO, BigInteger.ZERO);
    }

    String transactionHash = ((EthLog.LogObject) logResult.get()).getTransactionHash();
    if (transactionHash == null || transactionHash.isEmpty()) {
      log.warn("Transaction hash not found in log.");
      return new TransactionFees(BigInteger.ZERO, BigInteger.ZERO);
    }

    try {
      Optional<CustomTransactionReceipt> receiptOptional = web3j
        .getCustomTransactionReceipt(transactionHash)
        .send()
        .getTransactionReceipt();

      if (receiptOptional.isPresent()) {
        CustomTransactionReceipt receipt = receiptOptional.get();
        return new TransactionFees(receipt.getL1Fee(), receipt.getL2Fee());
      } else {
        log.warn("Custom transaction receipt not found for hash: {}", transactionHash);
        return new TransactionFees(BigInteger.ZERO, BigInteger.ZERO);
      }
    } catch (IOException e) {
      log.error("Error fetching custom transaction receipt for hash: {}", transactionHash, e);
      throw new RuntimeException(e);
    }
  }
}
