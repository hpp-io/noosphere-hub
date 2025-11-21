package io.hpp.noosphere.hub.config;

import static io.hpp.noosphere.hub.config.Constants.KEYSTORE_ETH_KEY_ALIAS;

import io.hpp.noosphere.hub.service.KeystoreService;
import io.hpp.noosphere.hub.service.blockchain.CustomWeb3jService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class Web3jConfig {

  private static final Logger LOG = LoggerFactory.getLogger(Web3jConfig.class);
  private final ApplicationProperties applicationProperties;
  private final KeystoreService keystoreService;

  public Web3jConfig(ApplicationProperties applicationProperties, KeystoreService keystoreService) {
    this.applicationProperties = applicationProperties;
    this.keystoreService = keystoreService;
  }

  @Bean
  public Web3j web3j(CustomWeb3jService customWeb3jService) {
    return customWeb3jService;
  }

  @Bean
  public CustomWeb3jService customWeb3jService(ScheduledExecutorService scheduledExecutorService) {
    String rpcUrl = applicationProperties.getBlockchain().getRpcUrl();

    long connectTimeout = getTimeoutValue(applicationProperties.getBlockchain().getConnectionTimeout(), 30000);
    long readTimeout = getTimeoutValue(applicationProperties.getBlockchain().getReadTimeout(), 30000);
    long writeTimeout = getTimeoutValue(applicationProperties.getBlockchain().getWriteTimeout(), 30000);

    LOG.info("rpcUrl=" + rpcUrl);

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
      .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
      .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
      .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

    HttpService httpService = new HttpService(rpcUrl, clientBuilder.build());
    return new CustomWeb3jService(httpService, 1000, scheduledExecutorService);
  }

  private long getTimeoutValue(Long timeout, int defaultValue) {
    return timeout != null ? timeout : defaultValue;
  }

  @Bean
  public Credentials credentials() {
    return keystoreService.getCredentials(KEYSTORE_ETH_KEY_ALIAS);
  }

  @Bean
  public CustomGasProvider gasProvider() {
    return new CustomGasProvider(applicationProperties.getBlockchain());
  }

  public static class CustomGasProvider extends DefaultGasProvider {

    private final ApplicationProperties.Blockchain blockchainConfig;

    public CustomGasProvider(ApplicationProperties.Blockchain blockchainConfig) {
      this.blockchainConfig = blockchainConfig;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
      BigInteger basePrice = super.getGasPrice(contractFunc);
      return basePrice.multiply(BigInteger.valueOf((long) (blockchainConfig.getGasPriceRatio() * 100))).divide(BigInteger.valueOf(100));
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
      BigInteger baseLimit = super.getGasLimit(contractFunc);
      return baseLimit.multiply(BigInteger.valueOf((long) (blockchainConfig.getGasLimitRatio() * 100))).divide(BigInteger.valueOf(100));
    }
  }

  @Bean
  public BigInteger chainId(Web3j web3j) {
    try {
      return web3j.ethChainId().send().getChainId();
    } catch (IOException e) {
      throw new RuntimeException("Retrieving Chain ID failed", e);
    }
  }

  @Bean
  public ScheduledExecutorService scheduledExecutorService() {
    return Executors.newSingleThreadScheduledExecutor();
  }
}
