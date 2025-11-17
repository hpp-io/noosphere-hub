package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.KEYSTORE_TYPE;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.config.ApplicationProperties.Keystore;
import io.hpp.noosphere.hub.security.KeystoreManager;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
public class KeystoreService {

  private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class);

  private final ApplicationProperties applicationProperties;
  private KeyStore keyStore;

  public KeystoreService(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
    try {
      this.keyStore = KeystoreManager.loadKeyStore(
        Path.of(applicationProperties.getKeystore().getPath()),
        applicationProperties.getKeystore().getStorePassword()
      );
    } catch (Exception e) {
      LOG.error("Failed to load the keystore. This is a fatal error for KeystoreService.", e);
      throw new IllegalStateException("Could not initialize KeystoreService", e);
    }
  }

  private String getKeystorePassword() {
    return applicationProperties.getKeystore().getStorePassword();
  }

  public String getSecretKey(String keyAlias) {
    try {
      return KeystoreManager.readSecretKeyAsUtf8String(this.keyStore, getKeystorePassword(), keyAlias);
    } catch (Exception e) {
      LOG.error("Failed to getSecretKey " + keyAlias, e);
    }
    return null;
  }

  public Credentials getCredentials(String keyAlias) {
    try {
      return KeystoreManager.readEthKeyFromSecret(this.keyStore, getKeystorePassword(), keyAlias);
    } catch (Exception e) {
      LOG.error("Failed to getCredentials " + keyAlias, e);
    }
    return null;
  }

  public String getEthPrivateKey(String keyAlias) {
    try {
      Credentials credentials = this.getCredentials(keyAlias);
      return KeystoreManager.readPrivateKeyAsHexString(credentials);
    } catch (Exception e) {
      LOG.error("Failed to getEthPrivateKey " + keyAlias, e);
    }
    return null;
  }
}
