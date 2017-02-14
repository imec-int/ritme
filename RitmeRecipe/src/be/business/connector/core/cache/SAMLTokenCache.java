/**
 * Copyright (C) 2010 Recip-e
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.business.connector.core.cache;

import be.business.connector.core.exceptions.IntegrationModuleEhealthException;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.technical.connector.utils.SAMLConverter;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.STSHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uz.ehealth.ritme.outbound.hospital.SessionService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.util.Calendar;


/**
 * The Class SAMLTokenCache.
 */
public class SAMLTokenCache {
	private final SessionService sessionService = PluginManager.get("ritme.outbound.hospital.sessionmanager", SessionService.class);


	
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(SAMLTokenCache.class);


	/**
	 * Instantiates a new sAML token cache.
	 */
	private SAMLTokenCache() {}

	private static class InstanceHolder {
		private static final SAMLTokenCache INSTANCE = new SAMLTokenCache();
	}

	/**
	 * Gets the single instance of SAMLTokenCache.
	 * 
	 * @return single instance of SAMLTokenCache
	 */
	public static SAMLTokenCache getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Gets the saml token.
	 * 
	 * @return the saml token
	 * @param nihiiOrg
	 */
	public Element getSamlToken(final String nihiiOrg) {

		return sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion();
	}
	
	
	/**
	 * Gets the saml token as xml.
	 * 
	 * @return the saml token as xml
	 * @throws IntegrationModuleException
	 * @param nihiiOrg
	 */
	public String getSamlTokenAsXML(final String nihiiOrg) throws IntegrationModuleException {
		if (sessionService.getSessionManager(nihiiOrg).getSession() != null) {
			return SAMLConverter.toXMLString(sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion());
		}
		return null;
	}

	/**
	 * Sets the saml token.
	 *
	 * @param samlToken the new saml token
	 * @param nihiiOrg
	 */
	public void setSamlToken(Element samlToken, final String nihiiOrg) {
		//noop session manages saml token
		//sessionService.getSessionManager(nihiiOrg).getSession().setSAMLToken(new SAMLTokenImpl(samlToken,sessionService.getSessionManager(nihiiOrg).getSession().getHolderOfKeyCredential()));
	}
	
	/**
	 * Checks if expired.
	 * 
	 * @return true, if successful
	 * @param nihiiOrg
	 */
	private boolean hasExpired(final String nihiiOrg) {
		if (sessionService.getSessionManager(nihiiOrg).getSession() != null && STSHelper.getConditions(sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion()).getLength() > 0) {
			// NotOnOrAfter
			Calendar calNotOnOrAfter = Calendar.getInstance();
			calNotOnOrAfter.setTime(STSHelper.getNotOnOrAfterConditions(sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion()).getTime());
			
			// now
			Calendar now = Calendar.getInstance();
			
			// check validity
			return !(now.before(calNotOnOrAfter));           	
		}
		return true;
	}
	
	/**
	 * Checks for valid attributes.
	 * 
	 * @return true, if successful
	 * @param nihiiOrg
	 */
	private boolean hasValidAttributes(final String nihiiOrg) {
		NodeList attributes = STSHelper.getAttributes(sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion());
		if (attributes == null || attributes.getLength() == 0) {
			return false;
		}
		for (int i=0; i < attributes.getLength(); i++) {
			Element attribute = (Element) attributes.item(i);
			LOG.debug("SAML AttributeName : "+ attribute.getAttribute("AttributeName") + " : TextContent : "+ attribute.getTextContent());
			if (attribute.getTextContent().trim().isEmpty()) {
				LOG.error("Empty SAML attribute designator, eHealth doesn't recognise you... contact eHealth");
				throw new IntegrationModuleEhealthException(I18nHelper.getLabel("error.saml.attribute",new String[]{attribute.getAttribute("AttributeName")}));
			}
		}
		return true;
	}
	
	/**
	 * Clear.
	 * @param nihiiOrg
	 */
	public void clear(final String nihiiOrg) {
		sessionService.getSessionManager(nihiiOrg).getSession().setSAMLToken(null);
	}
	
	/**
	 * Checks for session.
	 * 
	 * @return true, if successful
	 * @param nihiiOrg
	 */
	public boolean hasValidSession(final String nihiiOrg) {
		return sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken() != null && sessionService.getSessionManager(nihiiOrg).getSession().getSAMLToken().getAssertion() != null && !hasExpired(nihiiOrg) && hasValidAttributes(nihiiOrg);
	}
	
}
