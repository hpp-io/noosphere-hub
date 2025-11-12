package io.hpp.noosphere.hub.service;

import static io.hpp.noosphere.hub.config.Constants.KEYSTORE_TYPE;

import io.hpp.noosphere.hub.config.ApplicationProperties;
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

@Service
public class KeystoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class);

    private final ApplicationProperties applicationProperties;
    private KeyStore keyStore;

    public KeystoreService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        try {
            this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            try (InputStream fis = Files.newInputStream(Path.of(applicationProperties.getKeystore().getPath()))) {
                keyStore.load(fis, applicationProperties.getKeystore().getStorePassword().toCharArray());
            }
        } catch (Exception e) {
            LOG.error("Failed to load the keystore. This is a fatal error for KeystoreService.", e);
            throw new IllegalStateException("Could not initialize KeystoreService", e);
        }
    }

    private String getKeyPassword(String keyAlias) {
        return applicationProperties.getKeystore().getStorePassword();
    }

    public String getSecretKey(String keyAlias) {
        try {
            String keyPassword = getKeyPassword(keyAlias);
            if (CommonUtils.isValid(keyPassword) && keyStore != null) {
                KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyPassword.toCharArray());

                KeyStore.Entry entry = keyStore.getEntry(keyAlias, entryPassword);

                if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                    LOG.debug("Error: Entry with alias '" + keyAlias + "' is not a SecretKeyEntry.");
                    return null;
                }

                KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) entry;
                SecretKey secretKey = skEntry.getSecretKey();

                byte[] keyBytes = secretKey.getEncoded();
                return new String(Base64.getDecoder().decode(keyBytes), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOG.error("Failed to getSecretKey " + keyAlias, e);
        }
        return null;
    }
}
