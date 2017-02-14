package be.business.connector.projects.common.services.pcdh;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.ehealth.apb.gfddpp.services.pcdh.CheckAliveRequestType;
import be.ehealth.apb.gfddpp.services.pcdh.CheckAliveResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.ResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.SealedRequestType;
import be.ehealth.apb.gfddpp.services.pcdh.SealedResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.UploadPerformanceMetricRequestType;

public interface PcdhService {
	SealedResponseType getData(SealedRequestType sealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	SealedResponseType getDataTypes(SealedRequestType sealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	SealedResponseType getPharmacyDetails(SealedRequestType paramSealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	ResponseType uploadPerformanceMetric(UploadPerformanceMetricRequestType paramUploadPerformanceMetricRequestType, final String nihiiOrg) throws IntegrationModuleException;
	CheckAliveResponseType checkAlivePCDH(CheckAliveRequestType paramCheckAliveRequestType, final String nihiiOrg) throws IntegrationModuleException;
}
