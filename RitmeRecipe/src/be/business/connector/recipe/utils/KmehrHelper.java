/**
 * Copyright (C) 2010 Recip-e
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.business.connector.recipe.utils;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.IOUtils;
import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * The Class KmehrHelper.
 */
public class KmehrHelper {

    /**
     * The Constant W3C_XML_SCHEMA_NS_URI.
     */
    public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = Logger.getLogger(KmehrHelper.class);

    /**
     * The properties.
     */
    private Properties properties = null;


    static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";

    static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    /**
     * Instantiates a new kmehr helper.
     *
     * @param properties the properties
     */
    public KmehrHelper(Properties properties) {
        super();
        this.properties = properties;
    }

    /**
     * Assert valid kmehr prescription.
     *
     * @param xmlFile          the xml file
     * @param prescriptionType the prescription type
     * @throws IntegrationModuleException
     */
    public void assertValidKmehrPrescription(InputStream xmlFile, String prescriptionType) throws IntegrationModuleException {
        byte[] xmlStream = IOUtils.getBytes(xmlFile);
        assertValidKmehrPrescription(xmlStream, prescriptionType);
    }

    /**
     * Assert valid notification.
     *
     * @param xmlFile the xml file
     * @throws IntegrationModuleException
     */
    public void assertValidNotification(InputStream xmlFile) throws IntegrationModuleException {
        byte[] xmlStream = IOUtils.getBytes(xmlFile);
        assertValidNotification(xmlStream);
    }

    /**
     * Assert valid feedback.
     *
     * @param xmlFile the xml file
     * @throws IntegrationModuleException
     */
    public void assertValidFeedback(InputStream xmlFile) throws IntegrationModuleException {
        byte[] xmlStream = IOUtils.getBytes(xmlFile);
        assertValidFeedback(xmlStream);
    }

    /**
     * Assert valid notification.
     *
     * @param xmlDocument the xml document
     * @throws IntegrationModuleException
     */
    @Profiled(logFailuresSeparately = true, tag = "IntegrationModule#XMLNotificationValidation")
    public void assertValidNotification(byte[] xmlDocument) throws IntegrationModuleException {
        xsdValidate(xmlDocument, "notification.XSD");
    }

    /**
     * Assert valid notification.
     *
     * @param xmlDocument the xml document
     * @throws IntegrationModuleException
     */
    @Profiled(logFailuresSeparately = true, tag = "0.IntegrationModule#XMLFeedbackValidation")
    public void assertValidFeedback(byte[] xmlDocument) throws IntegrationModuleException {
        xsdValidate(xmlDocument, "feedback.XSD");
    }


    /**
     * Valid xml.
     *
     * @param xmlDocument      the xml document
     * @param prescriptionType the prescription type
     * @return true, if successful
     * @throws IntegrationModuleException
     */
    @Profiled(logFailuresSeparately = true, tag = "0.IntegrationModule#XMLPrescriptionValidation")
    public void assertValidKmehrPrescription(byte[] xmlDocument, String prescriptionType) throws IntegrationModuleException {
        try {
            xsdValidate(xmlDocument, "kmehr.XSD");

            DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
            factory2.setNamespaceAware(false);
            DocumentBuilder builder2 = factory2.newDocumentBuilder();
            Document doc2;
            doc2 = builder2.parse(new ByteArrayInputStream(xmlDocument));

            validateXpath(doc2, prescriptionType);
        } catch (SAXException e) {
            LOG.debug("Bad Prescription : " + xmlDocument);
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        } catch (IOException e) {
            LOG.debug("Bad Prescription : " + xmlDocument);
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        } catch (ParserConfigurationException e) {
            LOG.debug("Bad Prescription : " + xmlDocument);
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        }
    }

    /**
     * Xsd validate.
     *
     * @param xmlDocument     the xml document
     * @param xsdPropertyName the xsd property name
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException                 the sAX exception
     * @throws IOException                  Signals that an I/O exception has occurred.
     */
    private void xsdValidate(byte[] xmlDocument, String xsdPropertyName)
            throws IntegrationModuleException {
        try {


            String xsdName = properties.getProperty(xsdPropertyName);

            if (xsdName != null) {
                if (!new File(xsdName).exists()) {
                    try {
                        xsdName = this.getClass().getClassLoader().getResource(xsdName).toURI().toString();
                    } catch (URISyntaxException e) {
                        //do nothing
                    }
                } else {
                    xsdName = new File(xsdName).toURI().toString();
                }
            }

            try {
                if(xsdName==null){
                    throw new URISyntaxException("null","URI == null");
                }
                URI xsd = new URI(xsdName);
            } catch (URISyntaxException e) {
                LOG.error("kmehr.XSD property is not correctly set, invalid file " + xsdPropertyName + " = " + xsdName);
                throw new RuntimeException("kmehr.XSD property is not correctly set, invalid file " + xsdPropertyName + " = " + xsdName);

            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if (!factory.getClass().getName().startsWith("org.apache")) {
                LOG.warn("Non supported parser : " + factory.getClass().getName());
            }

            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA_NS_URI);
            factory.setAttribute(
                    JAXP_SCHEMA_SOURCE,
                    new String[]{ xsdName });

            DocumentBuilder builderNamespaceAware = factory.newDocumentBuilder();
            builderNamespaceAware.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException arg0) {
                    LOG.warn("XSD Warning", arg0);
                }

                @Override
                public void fatalError(SAXParseException arg0) throws SAXException {
                    LOG.error("XSD fatalError", arg0);
                    throw arg0;
                }

                @Override
                public void error(SAXParseException arg0) throws SAXException {
                    LOG.error("XSD error", arg0);
                    throw arg0;
                }
            });
            builderNamespaceAware.parse(new ByteArrayInputStream(xmlDocument));
        } catch (SAXException e) {
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        } catch (IOException e) {
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        } catch (ParserConfigurationException e) {
            throw new IntegrationModuleException(getLabel("error.xml.invalid"), e);
        }
    }


    /**
     * Gets the label.
     *
     * @param key the key
     * @return the label
     */

    private String getLabel(String key) {
        return I18nHelper.getLabel(key);
    }

    /**
     * Validate xpath.
     *
     * @param doc              the doc
     * @param prescriptionType the prescription type
     * @throws XPathExpressionException the x path expression exception
     */
    private void validateXpath(Document doc, String prescriptionType)
            throws IntegrationModuleException {
        try {

            int i = 1;
            String xpathConfig = null;
            do {
                String key = "kmehr.assert." + prescriptionType + "." + i;
                xpathConfig = (String) properties.get(key);

                // Expecting a property
                // kmehr.assert.PP.1 = "/xpath"/, min occurs, [max occurs]
                if (xpathConfig == null) {
                    break;
                }

                String[] xpathConfigs = xpathConfig.split(",");
                if (xpathConfigs.length < 2) {
                    throw new IntegrationModuleException("Invalid configuration : '" + key + "=" + xpathConfig + "'");
                }


                String xpathStr = xpathConfigs[0];
                int min = Integer.parseInt(xpathConfigs[1].trim());
                int max = xpathConfigs.length > 2 ? Integer.parseInt(xpathConfigs[2].trim()) : min;

                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList nodes;
                nodes = (NodeList) xpath.evaluate(xpathStr, doc, XPathConstants.NODESET);

                if (nodes.getLength() < min || nodes.getLength() > max) {
                    LOG.error("FAILED Xpath query : " + xpathStr);
                    throw new IntegrationModuleException(I18nHelper.getLabel("error.xml.invalid"));
                }
                i = i + 1;
            } while (true);
        } catch (XPathExpressionException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.xml.invalid"), e);
        }
    }


}
