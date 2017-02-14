package be.business.connector.core.utils;

import org.apache.log4j.Logger;


import be.business.connector.core.exceptions.IntegrationModuleEhealthException;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.exceptions.IntegrationModuleRuntimeException;

public class Exceptionutils {

	private static final Logger LOG = Logger.getLogger(Exceptionutils.class);

	public static void errorHandler(Throwable t) throws IntegrationModuleException, IntegrationModuleEhealthException, IntegrationModuleRuntimeException {
		if (t instanceof IntegrationModuleRuntimeException) {
			LOG.error("", t);
			throw (IntegrationModuleRuntimeException) t;
		} else if (t instanceof IntegrationModuleEhealthException) {
			LOG.error("", t);
			throw (IntegrationModuleEhealthException) t;
		} else if (t.getCause() instanceof IntegrationModuleEhealthException) {
			LOG.error("", t);
			throw (IntegrationModuleEhealthException) t.getCause();
		} else if (t instanceof IntegrationModuleException) {
			LOG.error("", t);
			throw (IntegrationModuleException) t;
		} else {
			LOG.error("", t);
			throw new IntegrationModuleException(I18nHelper.getLabel("error.technical"), t);
		}
	}

	public static void errorHandler(Throwable t, String errorMsg) throws IntegrationModuleException, IntegrationModuleEhealthException, IntegrationModuleRuntimeException {
		if (t instanceof IntegrationModuleRuntimeException) {
			LOG.error("", t);
			throw (IntegrationModuleRuntimeException) t;
		} else if (t instanceof IntegrationModuleEhealthException) {
			LOG.error("", t);
			throw (IntegrationModuleEhealthException) t;
		} else if (t.getCause() instanceof IntegrationModuleEhealthException) {
			LOG.error("", t);
			throw (IntegrationModuleEhealthException) t.getCause();
		} else if (t instanceof IntegrationModuleException) {
			LOG.error("", t);
			throw (IntegrationModuleException) t;
		}  else {
			LOG.error("", t);
			throw new IntegrationModuleException(I18nHelper.getLabel(errorMsg), t);
		}
	}

}
