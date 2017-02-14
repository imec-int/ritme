package be.business.connector.common;

import be.business.connector.core.exceptions.IntegrationModuleException;

public interface RequestorProvider {
	String getRequestorTypeInformation(final String nihiiOrg) throws IntegrationModuleException;

	String getRequestorIdInformation(final String nihiiOrg) throws IntegrationModuleException;
	
}
