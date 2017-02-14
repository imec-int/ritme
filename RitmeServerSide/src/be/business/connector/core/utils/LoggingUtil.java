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

import be.business.connector.core.handlers.LoggingHandler;
import be.business.connector.core.handlers.SoapFaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.xml.DOMConfigurator;

public class LoggingUtil {

	/** The LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(LoggingUtil.class);

	/**
	 * Adds the in out logger.
	 *
	 * @param bindingProvider the binding provider
	 */
	@SuppressWarnings("rawtypes")
	public static void addInOutLoggerHandler(Object port, boolean soapfaultHandler) {
		if (port instanceof BindingProvider) {
			BindingProvider bindingProvider = (BindingProvider) port;
			List<Handler> handlerChain = new ArrayList<Handler>();
			handlerChain.addAll(bindingProvider.getBinding().getHandlerChain());
			handlerChain.add(new LoggingHandler());
			if(soapfaultHandler){
				handlerChain.add(new SoapFaultHandler());
			}
			bindingProvider.getBinding().setHandlerChain(handlerChain);
		} else {
			LOG.warn("BindingProvider provider expected, get a " + port);
		}
	}

	/*

	public static void initLog4J(PropertyHandler propertyHandler) {
		System.out.println("****************  Init LOG4J");

		if (propertyHandler != null) {
			String log4jXmlPath = propertyHandler.getProperty("LOG4J", "log4j.xml");
			File file = new File(log4jXmlPath);
			if (file.exists()) {
		//		if (LOG.getUrl() == null || LOG.getUrl().getPath() != log4jXmlPath) {
					LogManager.resetConfiguration();
					DOMConfigurator.configure(file.getAbsolutePath());
					LOG.info("Loading log4j config from " + file.getAbsolutePath());
	//			}
			}
		}
	}
	*/
}
