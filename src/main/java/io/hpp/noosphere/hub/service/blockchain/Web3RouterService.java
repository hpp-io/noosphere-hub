package io.hpp.noosphere.hub.service.blockchain;

import static io.hpp.noosphere.hub.config.Constants.EMPTY_ADDRESS;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.config.Web3jConfig;
import io.hpp.noosphere.hub.service.blockchain.contract.Router;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;

@Service
public class Web3RouterService {

  private static final Logger log = LoggerFactory.getLogger(Web3RouterService.class);

  private static final String CONTRACT_NAME_WALLET_FACTORY = "WalletFactory";
  private final ApplicationProperties.Blockchain blockchainConfig;
  private final Web3j web3j;
  private final Credentials credentials;
  private final Web3jConfig.CustomGasProvider gasProvider;
  private final BigInteger chainId;
  private final Map<String, String> contractAddresses = new HashMap<>();
  private Router routerContract;

  public Web3RouterService(
    ApplicationProperties applicationProperties,
    Web3j web3j,
    Credentials credentials,
    Web3jConfig.CustomGasProvider gasProvider,
    BigInteger chainId
  ) {
    this.blockchainConfig = applicationProperties.getBlockchain();
    this.web3j = web3j;
    this.credentials = credentials;
    this.gasProvider = gasProvider;
    this.chainId = chainId;
  }

  @PostConstruct
  public void init() {
    String routerAddress = blockchainConfig.getRouterAddress();

    if (routerAddress == null || routerAddress.isEmpty()) {
      throw new IllegalStateException("Router contract configuration not found in noosphere config");
    }

    this.routerContract = Router.load(routerAddress, web3j, credentials, gasProvider);

    log.info("Initialized Router contract at address: {}", routerAddress);


  }


  public CompletableFuture<String> getContractAddress(String contractName) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        if (contractAddresses.containsKey(contractName)) {
          return contractAddresses.get(contractName);
        }

        byte[] contractNameBytes = contractName.getBytes();
        byte[] paddedBytes = new byte[32];
        System.arraycopy(contractNameBytes, 0, paddedBytes, 0, Math.min(contractNameBytes.length, 32));

        String address = routerContract.getContractById(paddedBytes).send();

        contractAddresses.put(contractName, address);

        log.info("Retrieved contract address for {}: {}", contractName, address);
        return address;
      } catch (Exception e) {
        log.error("Failed to get contract address for {}", contractName, e);
        throw new RuntimeException("Failed to get contract address", e);
      }
    });
  }


  public CompletableFuture<Boolean> isContractRegistered(String contractName) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        // Convert string to 32-byte array
        byte[] contractNameBytes = contractName.getBytes();
        byte[] paddedBytes = new byte[32];
        System.arraycopy(contractNameBytes, 0, paddedBytes, 0, Math.min(contractNameBytes.length, 32));

        String address = routerContract.getContractById(paddedBytes).send();
        return address != null && !address.equals(EMPTY_ADDRESS);
      } catch (Exception e) {
        log.error("Failed to check if contract {} is registered", contractName, e);
        return false;
      }
    });
  }

  public String getCachedContractAddress(String contractName) {
    return contractAddresses.get(contractName);
  }


  public CompletableFuture<Void> refreshCache() {
    return CompletableFuture.runAsync(() -> {
      contractAddresses.clear();
    });
  }

  public CompletableFuture<BigInteger> getLastSubscriptionId(Long blockNumber) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
          Router.FUNC_GETLASTSUBSCRIPTIONID,
          Collections.emptyList(),
          Collections.singletonList(new TypeReference<Uint64>() {
          })
        );
        String encodedFunction = FunctionEncoder.encode(function);

        DefaultBlockParameter blockParameter;
        if (blockNumber != null && blockNumber > 0) {
          log.debug("Querying last subscription ID from router contract at block {}", blockNumber);
          blockParameter = DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber));
        } else {
          log.debug("Querying last subscription ID from router contract at latest block...");
          blockParameter = DefaultBlockParameter.valueOf("latest");
        }

        org.web3j.protocol.core.methods.request.Transaction transaction =
          org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
            credentials.getAddress(),
            routerContract.getContractAddress(),
            encodedFunction
          );

        String result = web3j.ethCall(transaction, blockParameter).send().getValue();
        BigInteger lastId = (BigInteger) FunctionReturnDecoder.decode(result, function.getOutputParameters()).get(0).getValue();

        log.info("Retrieved last subscription ID: {} at block {}", lastId, blockNumber != null ? blockNumber : "latest");
        return lastId;
      } catch (Exception e) {
        log.error("Failed to get last subscription ID", e);
        throw new RuntimeException("Failed to get last subscription ID", e);
      }
    });
  }

  public CompletableFuture<BigInteger> getLastSubscriptionId() {
    return getLastSubscriptionId(null);
  }


  public BigInteger getChainId() {
    return this.chainId;
  }

  public CompletableFuture<String> getWalletFactoryAddress() {
    return CompletableFuture.supplyAsync(() -> {
      try {

        String address = contractAddresses.get(CONTRACT_NAME_WALLET_FACTORY);
        if (!CommonUtils.isValid(address)) {
          address = routerContract.getWalletFactory().send();
          if (CommonUtils.isValid(address)) {
            contractAddresses.put(CONTRACT_NAME_WALLET_FACTORY, address);
          }
        }
        log.info("Retrieved wallet factory address for {}", address);
        return address;
      } catch (Exception e) {
        log.error("Failed to get wallet factory address", e);
        throw new RuntimeException("Failed to get contract address", e);
      }
    });
  }

  public String getSyncWalletFactoryAddress() {
    try {
      CompletableFuture<String> walletFactoryAddressFuture = this.getWalletFactoryAddress().thenApply(walletFactoryAddress -> {
        if (CommonUtils.isValid(walletFactoryAddress)) {
          return walletFactoryAddress;
        } else {
          return EMPTY_ADDRESS;
        }
      });
      return walletFactoryAddressFuture.join();

    } catch (Exception e) {
      log.error("Failed to sync get wallet factory address", e);
      throw new RuntimeException("Failed to sync get contract address", e);
    }
  }

}
