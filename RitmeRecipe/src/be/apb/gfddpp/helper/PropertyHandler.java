package be.apb.gfddpp.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//import org.apache.log4j.Logger;

public class PropertyHandler {
//	private final static Logger LOG = Logger.getLogger(PropertyHandler.class);
	private static PropertyHandler instance = null; //Singleton pattern
	
	private Properties properties;
	
	public PropertyHandler(InputStream propertyfile) {
		try {
			properties = new Properties();
			
//			LOG.debug("Loading the default File");
			properties.load(getResourceAsStream("/smc.properties"));
			
//			LOG.debug("Loading the custom File");
			properties.load(propertyfile);
			
//			if (LOG.isDebugEnabled()) {
//				LOG.debug("Current folder is : " + new File(".").getCanonicalPath());
//				LOG.debug("Current properties are : ");
//				for (Object key : properties.keySet()) {
//					LOG.debug(key + " = " + properties.getProperty((String) key));
//				}
//			}
			
			instance = this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public PropertyHandler(String propertyfile)  {
		try {
			properties = new Properties();
			
//			LOG.debug("Loading the default File");
			properties.load(getResourceAsStream("/smc.properties"));
			
//			LOG.debug("Loading the custom File");
			properties.load(getResourceAsStream(propertyfile));
			
			File configFile = new File(propertyfile);
			if (configFile.exists()) {
				File confDir = configFile.getParentFile();
				if (confDir.isDirectory()) {
//					LOG.debug("Replacing %CONF% by '" + confDir.getCanonicalPath() + "'");
					for (Object key : properties.keySet()) {
						String value = properties.getProperty(key.toString());
						if (value != null && value.contains("%CONF%")) {
							properties.put(key, value.replaceAll("%CONF%", confDir.getCanonicalPath().replace('\\', '/')));
//							LOG.debug(key + " = " + properties.getProperty((String) key));
						}
					}
				}
			}
//			if (LOG.isDebugEnabled()) {
//				LOG.debug("Current folder is : " + new File(".").getCanonicalPath());
//				LOG.debug("Current properties are : ");
//				for (Object key : properties.keySet()) {
//					LOG.debug(key + " = " + properties.getProperty((String) key));
//				}
//			}
			
			instance = this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public InputStream getResourceAsStream(String path) throws IOException {
		File f = new File(path);
		if (f.exists()) {
//			LOG.info("Loading " + path + " from file system");
			return new FileInputStream(f);
		} else {
//			LOG.info("Loading " + path + " from classpath");
			InputStream stream = PropertyHandler.class.getResourceAsStream(path);
			if (stream == null) {
				throw new IOException("Invalid resource " + path);
			}
			return stream;
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
	public URL getURLProperty(String string) {
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
				
				URL url = new URL(wsdl);
				try {
//					LOG.debug("Checking connection with " + wsdl);
					url.openStream().close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return url;
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
//		return null;
	}
	
	/**
	 * Gets the integer property.
	 *
	 * @param string the string
	 * @param defaultValue the default value
	 * @return the integer property
	 */
	public Integer getIntegerProperty(String string, String defaultValue) {
		return Integer.parseInt(getProperty(string, defaultValue));
	}
	
	/**
	 * Gets the property.
	 *
	 * @param string the string
	 * @param defaultValue the default value
	 * @return the property
	 */
	public String getProperty(String string, String defaultValue) {
		if (properties == null) {
//			LOG.warn("Properties are not initialized");
			return defaultValue;
		}
		
		if (!properties.containsKey(string)) {
//			LOG.warn("Undefined property : " + string);
		}
		return properties.getProperty(string, defaultValue);
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



}

