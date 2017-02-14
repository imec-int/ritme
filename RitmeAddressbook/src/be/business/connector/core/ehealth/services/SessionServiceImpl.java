package be.business.connector.core.ehealth.services;

import be.business.connector.core.cache.SAMLTokenCache;
import be.business.connector.core.exceptions.IntegrationModuleEhealthException;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.PropertyHandler;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.sts.security.Credential;
import be.ehealth.technicalconnector.service.sts.security.KeyStoreInfo;
import be.ehealth.technicalconnector.service.sts.security.SAMLToken;
import be.ehealth.technicalconnector.service.sts.security.impl.KeyStoreCredential;
import be.ehealth.technicalconnector.service.sts.security.impl.SAMLTokenImpl;
import be.ehealth.technicalconnector.service.sts.utils.SAMLConverter;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.session.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class SessionManagerImpl.
 */
public class SessionServiceImpl implements SessionService {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(SessionServiceImpl.class);



	/** The session service. */
	private static SessionService sessionService;


	protected final static String SESSIONMANAGER_SAMLATTRIBUTE = "sessionmanager.samlattribute";

	protected final static String SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR = "sessionmanager.samlattributedesignator";

	protected final static String HUB_SESSIONMANAGER_SAMLATTRIBUTE = "hub.sessionmanager.samlattribute";

	protected final static String HUB_SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR = "hub.sessionmanager.samlattributedesignator";

	protected final static String SESSIONMANAGER_SAMLATTRIBUTE_MANDATE = "sessionmanager.samlattribute.mandate";

	protected final static String SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR_MANDATE = "sessionmanager.samlattributedesignator.mandate";

	private static final String EHEALTH_ERROR_CODE_WRONG_KEYSTORE_PASSWORD = "error.keystore.password";

	private SessionServiceImpl() {
	}

	/**
	 * Gets the singleton instance of SessionServiceImpl.
	 * 
	 * @return singleton instance of SessionServiceImpl
	 */
	public static SessionService getInstance() {
		if (sessionService == null) {
			sessionService = new SessionServiceImpl();
		}
		return sessionService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.technicalconnector.session.SessionManager#createSession(java.lang.String)
	 */
	@Override
	public String createSession(String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException {
		String samlToken = null;
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
				reloadSamlAttributesAndDesignators(HUB_SESSIONMANAGER_SAMLATTRIBUTE, HUB_SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR);
				reloadSamlAttributesAndDesignators(SESSIONMANAGER_SAMLATTRIBUTE, SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR);

			SessionItem sessionItem;
			if (!sessionmgmt.hasValidSession()) {
				sessionItem = sessionmgmt.createSession(holderOfKeyKeystorePassword, encryptionKeystorePassword);
			} else {
				sessionmgmt.unloadSession();
				sessionItem = sessionmgmt.createSession(holderOfKeyKeystorePassword, encryptionKeystorePassword);
			}
			samlToken = SAMLConverter.toXMLString(sessionItem.getSAMLToken().getAssertion());
			SAMLTokenCache.getInstance().setSamlToken(SAMLConverter.toElement(samlToken), null);
		} catch (SessionManagementException e) {
			LOG.error("Error creating session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		} catch (TechnicalConnectorException e) {
			LOG.error("Error creating session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		} catch (SOAPFaultException e) {
			LOG.error("Error creating fallback session", e);
			throw new IntegrationModuleEhealthException(I18nHelper.getLabel("error.ehealth.technical", new Object[] { e.getLocalizedMessage() }));
		}
		return samlToken;
	}

	@Override
	public String createMandateSession(String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException {
		String samlToken = null;
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
			reloadSamlAttributesAndDesignators(SESSIONMANAGER_SAMLATTRIBUTE_MANDATE, SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR_MANDATE);
			SessionItem sessionItem;
			if (!sessionmgmt.hasValidSession()) {
				sessionItem = sessionmgmt.createSession(holderOfKeyKeystorePassword, encryptionKeystorePassword);
			} else {
				sessionmgmt.unloadSession();
				sessionItem = sessionmgmt.createSession(holderOfKeyKeystorePassword, encryptionKeystorePassword);
			}
			samlToken = SAMLConverter.toXMLString(sessionItem.getSAMLToken().getAssertion());
			SAMLTokenCache.getInstance().setSamlToken(SAMLConverter.toElement(samlToken), null);
		} catch (SessionManagementException e) {
			LOG.error("Error creating session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		} catch (TechnicalConnectorException e) {
			LOG.error("Error creating session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		} catch (SOAPFaultException e) {
			LOG.error("Error creating fallback session", e);
			throw new IntegrationModuleEhealthException(I18nHelper.getLabel("error.ehealth.technical", new Object[] { e.getLocalizedMessage() }));
		}
		return samlToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.technicalconnector.session.SessionManager#createFallbackSession(java.lang.String, java.lang.String)
	 */
	@Override
	public String createFallbackSession(String identificationKeystorePassword, String holderOfKeyKeystorePassword, String encryptionKeystorePassword, final String nihiiOrg) throws IntegrationModuleException {
		String samlToken = null;
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
			reloadSamlAttributesAndDesignators(HUB_SESSIONMANAGER_SAMLATTRIBUTE, HUB_SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR);
			reloadSamlAttributesAndDesignators(SESSIONMANAGER_SAMLATTRIBUTE, SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR);
			SessionItem sessionItem;
			if (!sessionmgmt.hasValidSession()) {
				sessionItem = sessionmgmt.createFallbackSession(identificationKeystorePassword, holderOfKeyKeystorePassword, encryptionKeystorePassword);
			} else {
				sessionmgmt.unloadSession();
				sessionItem = sessionmgmt.createFallbackSession(identificationKeystorePassword, holderOfKeyKeystorePassword, encryptionKeystorePassword);
			}

			samlToken = SAMLConverter.toXMLString(sessionItem.getSAMLToken().getAssertion());

		} catch (SessionManagementException e) {
			LOG.error("Error creating fallback session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.fallback.session"), e);
		} catch (TechnicalConnectorException e) {
			LOG.error("Error creating fallback session", e);
			if (StringUtils.equals(EHEALTH_ERROR_CODE_WRONG_KEYSTORE_PASSWORD, e.getErrorCode())) {
				String keyStorePath = StringUtils.replace(StringUtils.substringBetween(e.getMessage(), "(", ")"), "path=", "");
				throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.wrong.keystore.password", new Object[] { keyStorePath }), e);
			}
			if (e.getCause() != null && e.getCause() instanceof TechnicalConnectorException && StringUtils.equals(EHEALTH_ERROR_CODE_WRONG_KEYSTORE_PASSWORD, ((TechnicalConnectorException) e.getCause()).getErrorCode())) {
				String keyStorePath = StringUtils.replace(StringUtils.substringBetween(((TechnicalConnectorException) e.getCause()).getMessage(), "(", ")"), "path=", "");
				throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.wrong.keystore.password", new Object[] { keyStorePath }), e);
			}
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.fallback.session"), e);
		} catch (SOAPFaultException e) {
			LOG.error("Error creating fallback session", e);
			throw new IntegrationModuleEhealthException(I18nHelper.getLabel("error.ehealth.technical", new Object[] { e.getLocalizedMessage() }));
		}
		return samlToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.technicalconnector.session.SessionManager#loadSession()
	 */
	@Override
	public void loadSession(String samlTokenString, String systemKeyStorePath, char[] systemKeyStorePassword, String privateKeyAlias, char[] privateKeyPassword, final String nihiiOrg) throws IntegrationModuleException {
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
			Element tokenElement = SAMLConverter.toElement(samlTokenString);

			KeyStoreInfo ksInfo = new KeyStoreInfo(systemKeyStorePath, systemKeyStorePassword, privateKeyAlias, privateKeyPassword);
			Credential authCredential = new KeyStoreCredential(ksInfo);

			SAMLToken samlToken = new SAMLTokenImpl(tokenElement, authCredential);

			if (sessionmgmt.hasValidSession()) {
				sessionmgmt.unloadSession();
			}

			final SessionItem sessionItem = sessionmgmt.getSession();
			sessionmgmt.loadSession(samlToken, new String(ksInfo.getKeystorePassword()), new String(ksInfo.getKeystorePassword()));

			SAMLTokenCache.getInstance().setSamlToken(tokenElement, null);
		} catch (TechnicalConnectorException e) {
			LOG.error("Error loading session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.load.session"), e);
		} catch (SOAPFaultException e) {
			LOG.error("Error creating fallback session", e);
			throw new IntegrationModuleEhealthException(I18nHelper.getLabel("error.ehealth.technical", new Object[] { e.getLocalizedMessage() }));
		}

	}

	/* (non-Javadoc)
	 * @see be.technicalconnector.services.SessionService#hasValidSession()
	 */
	@Override
	public void hasValidSession(final String nihiiOrg) throws IntegrationModuleException {
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
			
			if(!sessionmgmt.hasValidSession()){
				throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.invalid.session"));
			}
		} catch (SessionManagementException e) {
			LOG.error("Error has valid session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		}
		
	}
	
	@Override
	public boolean isValidSession(final String nihiiOrg) throws IntegrationModuleException {
		try {
			final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
			 return sessionmgmt.hasValidSession();
			
		} catch (SessionManagementException e) {
			LOG.error("Error has valid session", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.create.session"), e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see be.technicalconnector.services.SessionService#unloadSession()
	 */
	@Override
	public void unloadSession(final String nihiiOrg) {
		final SessionManager sessionmgmt = PluginManager.get("ritme.outbound.hospital.sessionmanager", uz.ehealth.ritme.outbound.hospital.SessionService.class).getSessionManager(nihiiOrg);
		sessionmgmt.unloadSession();
		SAMLTokenCache.getInstance().clear(null);
	}

	protected void removeAllSamlAttributesAndDesignators() {
		List<String> samlAttributesBefore = ConfigFactory.getConfigValidator(new ArrayList<String>()).getMatchingProperties(SESSIONMANAGER_SAMLATTRIBUTE);
		for (int i = 0; i < samlAttributesBefore.size(); i++) {
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(SESSIONMANAGER_SAMLATTRIBUTE + "." + (i + 1), null);
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(HUB_SESSIONMANAGER_SAMLATTRIBUTE + "." + (i + 1), null);
		}

		List<String> samlAttributeDesignatorsBefore = ConfigFactory.getConfigValidator(new ArrayList<String>()).getMatchingProperties(SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR);
		for (int i = 0; i < samlAttributeDesignatorsBefore.size(); i++) {
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR + "." + (i + 1), null);
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(HUB_SESSIONMANAGER_SAMLATTRIBUTEDESIGNATOR + "." + (i + 1), null);
		}

	}

	protected void reloadSamlAttributesAndDesignators(String samlAttribute, String samlAttributeDesginator) {

		removeAllSamlAttributesAndDesignators();

		List<String> samlAttributesInPropertyFile = PropertyHandler.getInstance().getMatchingProperties(samlAttribute);
		for (int i = 0; i < samlAttributesInPropertyFile.size(); i++) {
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(samlAttribute + "." + (i + 1), samlAttributesInPropertyFile.get(i));
		}

		List<String> samlAttributesAfterRefill = ConfigFactory.getConfigValidator(new ArrayList<String>()).getMatchingProperties(samlAttribute);
		LOG.debug("Number of samlAttributes after reload = " + samlAttributesAfterRefill.size());

		List<String> samlAttributeDesignatorsInPropertyFile = PropertyHandler.getInstance().getMatchingProperties(samlAttributeDesginator);
		for (int i = 0; i < samlAttributeDesignatorsInPropertyFile.size(); i++) {
			ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(samlAttributeDesginator + "." + (i + 1), samlAttributeDesignatorsInPropertyFile.get(i));
		}

		List<String> samlAttributeDesignatorsAfterRefill = ConfigFactory.getConfigValidator(new ArrayList<String>()).getMatchingProperties(samlAttributeDesginator);
		LOG.debug("Number of samlAttributeDesignators after reload = " + samlAttributeDesignatorsAfterRefill.size());

	}

}
