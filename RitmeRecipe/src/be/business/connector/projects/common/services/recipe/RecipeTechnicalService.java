package be.business.connector.projects.common.services.recipe;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.fgov.ehealth.recipe.protocol.v1.technical.UploadFileRequestType;
import be.fgov.ehealth.recipe.protocol.v1.technical.UploadFileResponseType;

public interface RecipeTechnicalService {
	  UploadFileResponseType uploadFilePatient(UploadFileRequestType paramUploadFileRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  UploadFileResponseType uploadFilePrescriber(UploadFileRequestType paramUploadFileRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  UploadFileResponseType uploadFileExecutor(UploadFileRequestType paramUploadFileRequestType, final String nihiiOrg) throws IntegrationModuleException;
}
