package be.business.connector.common;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.PropertyHandler;

public interface CommonIntegrationModule extends RequestorProvider {

    String createSession(final String nihiiOrg) throws IntegrationModuleException;

    void loadSession(String token, final String nihiiOrg) throws IntegrationModuleException;

    void setProperty(String key, String value);

    void setSystemProperty(String key, String value);

    void unloadSession(final String nihiiOrg) throws IntegrationModuleException;

    String createFallbackSession(String niss, String passphrase, final String nihiiOrg) throws IntegrationModuleException;

    public boolean hasValidSession(final String nihiiOrg);

    void assertValidSession(final String nihiiOrg) throws IntegrationModuleException;

    void setSystemKeystoreProperties(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreRizivKBO, final String nihiiOrg);

    void setOldSystemKeystoreProperties(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreRizivKBO, final String nihiiOrg);

    EncryptionUtils getEncryptionUtils(final String nihiiOrg);

    PropertyHandler getPropertyHandler();

    public String getNiss();

    String createMandatePhysicalPerson(final String nihiiOrg) throws IntegrationModuleException;
}
