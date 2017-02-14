package be.business.connector.core.ehealth.services;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;

/**
 * The Interface SessionManager.
 */
public interface SessionService {

	/**
	 * Creates the session.
	 * 
	 * @param holderOfKeyKeystorePassword
	 *            the keystore password for keystore defined in the sessionmanager.holderofkey.keystore property
	 * @param encryptionKeystorePassword
	 *            the keystore password for the keystore defined in the sessionmanager.encryption.keystore property
	 * @param nihiiOrg
	 * @return the session string
	 * @throws TechnicalConnectorException
	 *             the technical connector exception
	 */
	String createSession(String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException;

	/**
	 * Creates the mandate session.
	 *
	 * @param holderOfKeyKeystorePassword the holder of key keystore password
	 * @param encryptionKeystorePassword the encryption keystore password
	 * @param nihiiOrg
	 * @return the string
	 * @throws TechnicalConnectorException the technical connector exception
	 */
	String createMandateSession(String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException;
	
	
	/**
	 * Creates the fallback session.
	 * 
	 * @param identificationKeystorePassword
	 *            the identification keystore password
	 * @param holderOfKeyKeystorePassword
	 *            the keystore password for keystore defined in the sessionmanager.holderofkey.keystore property
	 * @param encryptionKeystorePassword
	 *            the keystore password for the keystore defined in the sessionmanager.encryption.keystore property
	 * @param nihiiOrg
	 * @return the session string
	 * @throws TechnicalConnectorException
	 *             the technical connector exception
	 */
	String createFallbackSession(String identificationKeystorePassword, String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException;

	/**
	 * Load session.
	 *
	 * @param samlTokenString the saml token string
	 * @param systemKeyStorePath the system key store path
	 * @param systemKeyStorePassword the system key store password
	 * @param privateKeyAlias the private key alias
	 * @param privateKeyPassword the private key password
	 * @param nihiiOrg
	 * @return the session string
	 * @throws TechnicalConnectorException the technical connector exception
	 */
	void loadSession(String samlTokenString, String systemKeyStorePath, char[] systemKeyStorePassword, String privateKeyAlias, char[] privateKeyPassword, final String nihiiOrg) throws IntegrationModuleException;

	/**
	 * Checks for valid session.
	 * @param nihiiOrg
	 */
	void hasValidSession(final String nihiiOrg) throws IntegrationModuleException;
	
	/**
	 * Unload session.
	 * @param nihiiOrg
	 */
	void unloadSession(final String nihiiOrg);

	boolean isValidSession(final String nihiiOrg) throws IntegrationModuleException;

}
