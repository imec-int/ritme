/**
 * Copyright (C) 2010 Recip-e
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.business.connector.core.utils;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyHandler.class);
    private static PropertyHandler instance = null; // Singleton pattern

    private Properties properties;
    private Properties originalProperties;

    //Mocking should only need the default connector properties loaded.
    public PropertyHandler() throws IntegrationModuleException {
        this("", true);
    }

    public PropertyHandler(String propertyfile) throws IntegrationModuleException {
        this(propertyfile, false);
    }

    private PropertyHandler(String propertyfile, boolean onlyLoadDefaultProperties) throws IntegrationModuleException {
        try {
            properties = new Properties();

            LOG.debug("Loading the default File");
            properties.load(IOUtils.getResourceAsStream("/connector-default.properties"));

            if (!onlyLoadDefaultProperties) {
                LOG.debug("Loading the custom File");
                properties.load(IOUtils.getResourceAsStream(propertyfile));

                try {
                    ConfigFactory.setConfigLocation(propertyfile);
                } catch (TechnicalConnectorException e) {
                    LOG.error("", e);
                    throw new IntegrationModuleException(I18nHelper.getLabel("error.propertyfile.technicalconnector"), e);
                }

                File configFile = new File(propertyfile);
                if (configFile.exists()) {
                    File confDir = configFile.getParentFile();
                    if (confDir.isDirectory()) {
                        LOG.debug("Replacing %CONF% by '" + confDir.getCanonicalPath() + "'");
                        for (Object key : properties.keySet()) {
                            String value = properties.getProperty(key.toString());
                            if (value != null && value.contains("%CONF%")) {
                                properties.put(key, value.replaceAll("%CONF%", confDir.getCanonicalPath().replace('\\', '/')));
                                ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(key.toString(), value.replaceAll("%CONF%", confDir.getCanonicalPath().replace('\\', '/')));
                                LOG.debug(key + " = " + properties.getProperty((String) key));
                            }
                        }
                    }
                }
            }
            reloadEhealthConfig();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Current folder is : " + new File(".").getCanonicalPath());
                LOG.debug("Current properties are : ");
                for (Object key : properties.keySet()) {
                    LOG.debug(key + " = " + properties.getProperty((String) key));
                }
            }
            instance = this;
            //LoggingUtil.initLog4J(this);
        } catch (IOException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.propertyfile"), e);
        }
    }

    public PropertyHandler(String propertyfileUrl, String urlConf) throws IntegrationModuleException {
        try {
            properties = new Properties();
            LOG.debug("Loading the default File");
            properties.load(IOUtils.getResourceAsStream("/connector-default.properties"));
            LOG.debug("Loading the custom File");
            InputStream stream = IOUtils.getResourceAsStream(propertyfileUrl);
            properties.load(stream);

            try {
                ConfigFactory.setConfigLocation(propertyfileUrl);
            } catch (TechnicalConnectorException e) {
                LOG.error("", e);
                throw new IntegrationModuleException(I18nHelper.getLabel("error.propertyfile.technicalconnector"), e);
            }

            if (urlConf != null) {
                for (Object key : properties.keySet()) {
                    String value = properties.getProperty(key.toString());
                    if (value != null && value.contains("%CONF%")) {
                        properties.put(key, value.replaceAll("%CONF%", urlConf));
                        LOG.debug(key + " = " + properties.getProperty((String) key));
                    }
                }
            }
            reloadEhealthConfig();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Current folder is : " + new File(".").getCanonicalPath());
                LOG.debug("Current properties are : ");
                for (Object key : properties.keySet()) {
                    LOG.debug(key + " = " + properties.getProperty((String) key));
                }
            }

            instance = this;
            //LoggingUtil.initLog4J(this);
        } catch (IOException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.propertyfile"), e);
        }
    }

    private void reloadEhealthConfig() throws IntegrationModuleException {
        try {
            ConfigFactory.getConfigValidator().invalidate();
            ConfigFactory.getConfigValidator().reload();
        } catch (TechnicalConnectorException ex) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.propertyfile.technicalconnector"), ex);
        }
    }

    public PropertyHandler(Properties properties) {
        this.properties = properties;
        instance = this;
    }

    public static PropertyHandler getInstance() {
        return instance;
    }

    public String getProperty(String string) {
        return getProperty(string, null);
    }

    /**
     * Gets the integer property.
     *
     * @param string the string
     * @return the integer property
     */
    public Integer getIntegerProperty(String string) {
        return Integer.parseInt(getProperty(string));
    }

    /**
     * Gets the uRL property.
     *
     * @param string the string
     * @return the uRL property
     */
    public URL getURLProperty(String string) throws IntegrationModuleException {
        try {
            String prop = getProperty(string);
            if (prop != null && prop.contains("META-INF")) {
                return this.getClass().getResource(prop);
            }

            String wsdl = getProperty(string);
            if (wsdl == null) {
                return null;
            }

            File f = new File(wsdl);
            if (f.exists()) {
                return f.toURI().toURL();
            } else {
                URL url = null;

                try {
                    url = new URL(wsdl);
                } catch (MalformedURLException e) {
                    throw new IntegrationModuleException(I18nHelper.getLabel("wsdl.not.found", new String[]{prop}));
                }
                try {
                    LOG.debug("Checking connection with " + wsdl);
                    url.openStream().close();
                } catch (IOException e) {
                    throw new IntegrationModuleException(I18nHelper.getLabel("error.could.not.reach.url", new Object[]{wsdl}), e);
                }
                return url;
            }
        } catch (Throwable t) {
            RitmeExceptionUtils.errorHandler(t);
        }
        return null;
    }

    /**
     * Gets the integer property.
     *
     * @param string       the string
     * @param defaultValue the default value
     * @return the integer property
     */
    public Integer getIntegerProperty(String string, String defaultValue) {
        return Integer.parseInt(getProperty(string, defaultValue));
    }

    /**
     * Gets the property.
     *
     * @param string       the string
     * @param defaultValue the default value
     * @return the property
     */
    public String getProperty(String string, String defaultValue) {
        if (properties == null) {
            LOG.warn("Properties are not initialized");
            return defaultValue;
        }

        if (!properties.containsKey(string)) {
            LOG.warn("Undefined property : " + string);
        }
        String propertyValue = properties.getProperty(string, defaultValue);

        // Printing a password in the logfile is a security issue
        if (!StringUtils.contains(string.toLowerCase(), "password")) {
            LOG.info("loading property " + string + " DefaultValue : " + defaultValue + " value returned : " + propertyValue);
        }
        return propertyValue != null ? propertyValue.trim() : propertyValue;
    }

    public boolean hasProperty(String key) {
        return properties != null && properties.containsKey(key);
    }

    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Gets the properties that match a root key.
     *
     * @param rootKey the root key
     * @return the properties
     */
    public List<String> getMatchingProperties(String rootKey) {
        int i = 1;
        List<String> ret = new ArrayList<String>();
        while (true) {
            String key = rootKey + "." + i;
            if (properties.getProperty(key) == null) {
                return ret;
            } else {
                ret.add(getProperty(key));
            }
            i++;
        }
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void setDefaultSessionProperties(String niss) {
        if (originalProperties != null) {
            for (Entry<Object, Object> entry : originalProperties.entrySet()) {
                properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
            originalProperties = null;
        }
    }
}
