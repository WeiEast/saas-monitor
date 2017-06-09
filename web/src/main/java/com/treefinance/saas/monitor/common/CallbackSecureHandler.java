package com.treefinance.saas.monitor.common;

import com.datatrees.toolkits.util.crypto.RSA;
import com.datatrees.toolkits.util.crypto.core.Decryptor;
import com.datatrees.toolkits.util.crypto.core.Encryptor;
import com.datatrees.toolkits.util.json.Jackson;
import com.treefinance.saas.monitor.web.auth.exception.CallbackEncryptException;
import com.treefinance.saas.monitor.web.auth.exception.CryptorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by luoyihua on 2017/5/10.
 */
@Service
public class CallbackSecureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CallbackSecureHandler.class);

    private Encryptor getEncryptor(String publicKey) {
        try {
            if (StringUtils.isEmpty(publicKey)) {
                throw new IllegalArgumentException("Can not find commercial tenant's public key.");
            }

            return RSA.createEncryptor(publicKey);
        } catch (Exception e) {
            throw new CryptorException(
                    "Error creating Encryptor with publicKey '" + publicKey + " to encrypt callback.", e);
        }
    }

    public String encrypt(Object data, String publicKey) throws CallbackEncryptException {
        if (data == null) {
            return null;
        }
        String encryptedData = encryptResult(data, publicKey);
        logger.debug("Finish encrypting callback for encryptedData '{}'.", encryptedData);

        return encryptedData;
    }

    private String encryptResult(Object data, String publicKey) throws CallbackEncryptException {
        Encryptor encryptor = getEncryptor(publicKey);
        try {
            byte[] json = Jackson.toJSONByteArray(data);
            return encryptor.encryptAsBase64String(json);
        } catch (Exception e) {
            throw new CallbackEncryptException("Error encrypting callback data", e);
        }
    }

    private Decryptor getDecryptor(String privateKey) {
        try {
            if (StringUtils.isEmpty(privateKey)) {
                throw new IllegalArgumentException("Can not find commercial tenant's private key.");
            }
            return RSA.createDecryptor(privateKey);
        } catch (Exception e) {
            throw new CryptorException(
                    "Error creating Decryptor with privateKey '" + privateKey + " to encrypt callback.", e);
        }
    }

    private String decryptResult(Object data, String privateKey) throws CallbackEncryptException {
        Decryptor decryptor = getDecryptor(privateKey);
        try {
            byte[] json = Jackson.toJSONByteArray(data);
            return decryptor.decryptWithBase64AsString(json);
        } catch (Exception e) {
            throw new CallbackEncryptException("Error decrypting callback data", e);
        }
    }

    public String decrypt(Object data, String privateKey) throws CallbackEncryptException {
        if (data == null) {
            return null;
        }
        String decryptedData = decryptResult(data, privateKey);
        logger.debug("Finish decrypting callback for decryptedData '{}'.", decryptedData);

        return decryptedData;
    }
}
