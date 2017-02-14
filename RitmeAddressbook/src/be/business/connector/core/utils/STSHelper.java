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

package be.business.connector.core.utils;

import be.business.connector.core.exceptions.IntegrationModuleException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * The Class STSHelper.
 */
public class STSHelper {

	/** The Constant SAML_ATTRIBUTE_NAMESPACE. */
	private static final String SAML_ATTRIBUTE_NAMESPACE = "AttributeNamespace";

	/** The Constant SAML_ATTRIBUTE_NAME. */
	private static final String SAML_ATTRIBUTE_NAME = "AttributeName";

	/** The Constant SAML_CONDITIONS. */
	public static final String SAML_CONDITIONS = "Conditions";
	
	/** The Constant SAML_NOTONORAFTER. */
	public static final String SAML_NOTONORAFTER = "NotOnOrAfter";
	
	/** The Constant SAML_SUCCESS. */
	public static final String SAML_SUCCESS =  "samlp:Success";
	
	/** The Constant SAML_STATUSCODE. */
	public static final String SAML_STATUSCODE = "StatusCode";
	
	/** The Constant SAML_STATUSMESSAGE. */
	public static final String SAML_STATUSMESSAGE = "StatusMessage";
	
	/** The Constant SAML_VALUE. */
	public static final String SAML_VALUE = "Value";
	
	/** The Constant SAML_ASSERTION. */
	public static final String SAML_ASSERTION = "Assertion";
	
	/** The Constant SAML_ATTRIBUTESTATEMENT. */
	public static final String SAML_ATTRIBUTESTATEMENT = "AttributeStatement";
	
	/** The Constant SAML_ATTRIBUTE. */
	public static final String SAML_ATTRIBUTE = "Attribute";
	
	
	/**
	 * Gets the status code.
	 * 
	 * @param stsResponse the sts response
	 * @return the status code
	 */
	public static String getStatusCode(Element stsResponse) {
		return stsResponse.getElementsByTagName(SAML_STATUSCODE).item(0).getAttributes().getNamedItem(SAML_VALUE).getNodeValue();
	}
	
	
	/**
	 * Gets the status message.
	 *
	 * @param stsResponse the sts response
	 * @return the status message
	 */
	public static String getStatusMessage(Element stsResponse) {
		try{
			return stsResponse.getElementsByTagName(SAML_STATUSMESSAGE).item(0).getTextContent();
		}catch(Throwable t){
			return null;
		}
	}
	/**
	 * Gets the not on or after conditions.
	 * 
	 * @param stsResponse the sts response
	 * @return the not on or after conditions
	 */
	public static Calendar getNotOnOrAfterConditions(Element stsResponse) {
		return DatatypeConverter.parseDate(stsResponse.getElementsByTagName(SAML_CONDITIONS).item(0).getAttributes().getNamedItem(SAML_NOTONORAFTER).getTextContent());
	}
	
	/**
	 * Gets the conditions.
	 * 
	 * @param stsResponse the sts response
	 * @return the conditions
	 */
	public static NodeList getConditions(Element stsResponse) {
		return stsResponse.getElementsByTagName(SAML_CONDITIONS);
	}
	
	/**
	 * Gets the attributes.
	 * 
	 * @param stsResponse the sts response
	 * @return the attributes
	 */
	public static NodeList getAttributes(Element stsResponse) {
		return ((Element)stsResponse.getElementsByTagName(SAML_ATTRIBUTESTATEMENT).item(0)).getElementsByTagName(SAML_ATTRIBUTE);		
	}
	
	/**
	 * Gets the assertion.
	 * 
	 * @param stsResponse the sts response
	 * @return the assertion
	 */
	public static Element getAssertion(Element stsResponse) {
		return (Element) stsResponse.getElementsByTagName(SAML_ASSERTION).item(0);
	}
	
	
	/**
	 * Gets the nihii.
	 *
	 * @param element the element
	 * @return the nihii
	 * @throws IntegrationModuleException 
	 */
	public static String getNihii(Element element) throws IntegrationModuleException {
		NodeList attributes = element.getElementsByTagName(SAML_ATTRIBUTE);
		if( attributes.getLength() == 0 ){
			attributes = element.getElementsByTagNameNS( 
					"urn:oasis:names:tc:SAML:1.0:assertion",SAML_ATTRIBUTE);
			if( attributes.getLength() == 0 ){
				return null;
			}
		}
		for(int i=0;i<attributes.getLength();i++){
			Node node = attributes.item(i);
			String attributeName = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAME).getTextContent();
			String attributeNamespace = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAMESPACE).getTextContent();
			// Doctor
			if( "urn:be:fgov:person:ssin:ehealth:1.0:doctor:nihii11".equals(attributeName) && 
					"urn:be:fgov:certified-namespace:ehealth".equals(attributeNamespace)){
				return node.getTextContent().trim();
			}
			// Pharmacist
			if( "urn:be:fgov:ehealth:1.0:pharmacy:nihii-number".equals(attributeName) && 
					"urn:be:fgov:identification-namespace".equals(attributeNamespace)){
				return(node.getTextContent().trim());
			}
			// Hospital
			if( "urn:be:fgov:ehealth:1.0:hospital:nihii-number".equals(attributeName) &&
					"urn:be:fgov:identification-namespace".equals(attributeNamespace)){
				return(node.getTextContent().trim());
			}
		}
		throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.saml.nihii.not.found"));
	}
	
	public static String getType(Element element) throws IntegrationModuleException {
		NodeList attributes = element.getElementsByTagName(SAML_ATTRIBUTE);
		if( attributes.getLength() == 0 ){
			attributes = element.getElementsByTagNameNS( 
					"urn:oasis:names:tc:SAML:1.0:assertion",SAML_ATTRIBUTE);
			if( attributes.getLength() == 0 ){
				return null;
			}
		}
		for(int i=0;i<attributes.getLength();i++){
			Node node = attributes.item(i);
			String attributeName = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAME).getTextContent();
			String attributeNamespace = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAMESPACE).getTextContent();
		}
		throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.saml.type.not.found"));
	}
	
	/**
	 * Gets the niss.
	 *
	 * @param element the element
	 * @return the niss
	 */
	public static String getNiss(Element element) {
		NodeList attributes = element.getElementsByTagName(SAML_ATTRIBUTE);
		if( attributes.getLength() == 0 ){
			attributes = element.getElementsByTagNameNS( 
					"urn:oasis:names:tc:SAML:1.0:assertion",SAML_ATTRIBUTE);
			if( attributes.getLength() == 0 ){
				return null;
			}
		}
		for(int i=0;i<attributes.getLength();i++){
			Node node = attributes.item(i);
			String attributeName = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAME).getTextContent();
			String attributeNamespace = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAMESPACE).getTextContent();
			if( "urn:be:fgov:person:ssin".equals(attributeName) && 
					"urn:be:fgov:identification-namespace".equals(attributeNamespace)){
				return node.getTextContent().trim();
			}
		}
		return null;
	}
	
	/**
	 * Convert.
	 * 
	 * @param stsResponse the sts response
	 * @return the element
	 * @throws IntegrationModuleException the integration module exception
	 */
	public static Element convert(Source stsResponse) throws IntegrationModuleException {
		try {
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(stsResponse, result);
			final String xmlResponse = stringWriter.getBuffer().toString();
			return SAML10Converter.toElement(xmlResponse);
		} catch (TransformerException e) {
			throw new IntegrationModuleException(e);
        }
	}
}
