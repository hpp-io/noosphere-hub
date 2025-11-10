package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.KEYSTORE_TYPE;

import io.hpp.noosphere.hub.config.ApplicationProperties;
import io.hpp.noosphere.hub.service.uil.CommonUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KeystoreService {

  private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class);

  private final ApplicationProperties applicationProperties;
  private KeyStore keyStore;

  public KeystoreService(
    ApplicationProperties applicationProperties
  ) {
    this.applicationProperties = applicationProperties;
    try {
      this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
      File keyStoreFile = new File(applicationProperties.getKeystore().getPath());
      try (InputStream fis = new FileInputStream(keyStoreFile.getAbsoluteFile())) {
        keyStore.load(fis, applicationProperties.getKeystore().getStorePassword().toCharArray());
      }
    } catch (Exception e) {

    }

  }

  private String getKeyPassword(String keyAlias) {
    return applicationProperties.getKeystore().getStorePassword();
  }

  public String getSecretKey(String keyAlias) {
    try {
      String keyPassword = getKeyPassword(keyAlias);
      if (CommonUtils.isValid(keyPassword) && keyStore != null) {
        KeyStore.ProtectionParameter entryPassword =
          new KeyStore.PasswordProtection(keyPassword.toCharArray());

        KeyStore.Entry entry = keyStore.getEntry(keyAlias, entryPassword);

        if (!(entry instanceof KeyStore.SecretKeyEntry)) {
          LOG.debug("Error: Entry with alias '" + keyAlias + "' is not a SecretKeyEntry.");
          return null;
        }

        KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) entry;
        SecretKey secretKey = skEntry.getSecretKey();

        byte[] keyBytes = secretKey.getEncoded();
//      LOG.debug("Key length: " + keyBytes.length + " bytes");

//      String base64Key = Base64.getEncoder().encodeToString(keyBytes);
//      LOG.debug("Key (Base64): " + base64Key);
//
        String hexKey = CommonUtils.bytesToHex(keyBytes);
//      LOG.debug("Key (Hex): " + hexKey);

        return hexKey;
      }
    } catch (Exception e) {
      LOG.error("Failed to getSecretKey " + keyAlias, e);
    }
    return null;
  }


}
