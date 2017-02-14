package be.business.connector.core.technical.connector.utils;

import java.util.List;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.EncryptionUtils;
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult;
import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealerException;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionToken;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class Crypto {

    private final static Logger LOG = Logger.getLogger(Crypto.class);

    public Crypto() {

    }

    public static byte[] seal(EncryptionToken etk, String data, final String orgNihii) throws IntegrationModuleException {
        return seal(etk, data.getBytes(), orgNihii);
    }

    public static byte[] seal(EncryptionToken etk, byte[] data, final String orgNihii) throws IntegrationModuleException {
        try {
            DataSealer dataSealer = EncryptionUtils.getInstance(orgNihii).initSealing();
            return dataSealer.seal(etk, data);
        } catch (KeyStoreException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (UnrecoverableKeyException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (CertificateException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (IOException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (DataSealerException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        }
    }

    public static byte[] seal(byte[] data, SecretKey secretKey, String keyId, final String nihiiOrg) throws IntegrationModuleException {
        try {
            DataSealer dataSealer = EncryptionUtils.getInstance(nihiiOrg).initSealing();
            return dataSealer.seal(data, secretKey, keyId);
        } catch (KeyStoreException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (UnrecoverableKeyException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (CertificateException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (IOException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (DataSealerException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        }
    }

    public static byte[] seal(List<EncryptionToken> etks, byte[] data, final String nihiiOrg) throws IntegrationModuleException {
            return seal(etks.get(0), data, nihiiOrg);
    }

    public static byte[] unseal(byte[] data, final String nihiiOrg) throws IntegrationModuleException {
        try {
            EncryptionUtils encryptionUtils = EncryptionUtils.getInstance(nihiiOrg);
            DataUnsealer dataUnsealer = encryptionUtils.initUnsealing();
            return encryptionUtils.unsealingData(dataUnsealer.unseal(data));
        } catch (KeyStoreException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (UnrecoverableKeyException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (CertificateException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (IOException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        }
    }

    public static byte[] unseal(SecretKey secretKey, byte[] data, final String nihiiOrg) throws IntegrationModuleException {
        try {
            EncryptionUtils encryptionUtils = EncryptionUtils.getInstance(nihiiOrg);
            DataUnsealer dataUnsealer = encryptionUtils.initUnsealing();
            return encryptionUtils.unsealingData(dataUnsealer.unseal(data, secretKey));
        } catch (KeyStoreException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (UnrecoverableKeyException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (CertificateException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        } catch (IOException ex) {
            throw new IntegrationModuleException("technical.connector.error.data.seal", ex);
        }
    }

    public static byte[] unsealForUnknown(KeyResult keyResult, byte[] data, final String nihiiOrg) throws IntegrationModuleException {
        return unseal(keyResult.getSecretKey(), data, nihiiOrg);
    }
}
