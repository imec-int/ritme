package be.business.connector.projects.common.services.pcdh;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import be.ehealth.apb.gfddpp.services.pcdh.CheckAliveRequestType;
import be.ehealth.apb.gfddpp.services.pcdh.CheckAliveResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.ObjectFactory;
import be.ehealth.apb.gfddpp.services.pcdh.ResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.SealedRequestType;
import be.ehealth.apb.gfddpp.services.pcdh.SealedResponseType;
import be.ehealth.apb.gfddpp.services.pcdh.UploadPerformanceMetricRequestType;

public class PcdhServiceImpl implements PcdhService {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(PcdhServiceImpl.class);

	public static final String ENDPOINT_NAME = "endpoint.pcdh";

	private static PcdhService pcdhService;

	private PcdhServiceImpl() {
	}

	/**
	 * Gets the singleton instance of PcdhServiceImpl.
	 * 
	 * @return singleton instance of PcdhServiceImpl
	 */
	public static PcdhService getInstance() {
		if (pcdhService == null) {
			pcdhService = new PcdhServiceImpl();
		}
		return pcdhService;
	}

	@Override
	public SealedResponseType getDataTypes(SealedRequestType sealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactoryPcdh = new ObjectFactory();
		JAXBElement<SealedRequestType> jb = objectFactoryPcdh.createGetDataTypesRequest(sealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedRequestType.class, SealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, false, false, nihiiOrg);
	}

	@Override
	public SealedResponseType getData(SealedRequestType sealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactoryPcdh = new ObjectFactory();
		JAXBElement<SealedRequestType> jb = objectFactoryPcdh.createGetDataRequest(sealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedRequestType.class, SealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, false, false, nihiiOrg);
	}

	@Override
	public SealedResponseType getPharmacyDetails(SealedRequestType sealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactoryPcdh = new ObjectFactory();
		JAXBElement<SealedRequestType> jb = objectFactoryPcdh.createGetPharmacyDetailsRequest(sealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedRequestType.class, SealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, false, false, nihiiOrg);
	}

	@Override
	public ResponseType uploadPerformanceMetric(UploadPerformanceMetricRequestType uploadPerformanceMetricRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactoryPcdh = new ObjectFactory();
		JAXBElement<UploadPerformanceMetricRequestType> jb = objectFactoryPcdh.createUploadPerformanceMetricRequest(uploadPerformanceMetricRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, UploadPerformanceMetricRequestType.class, SealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, false, false, nihiiOrg);
	}

	@Override
	public CheckAliveResponseType checkAlivePCDH(CheckAliveRequestType checkAliveRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactoryPcdh = new ObjectFactory();
		JAXBElement<CheckAliveRequestType> jb = objectFactoryPcdh.createCheckAlivePCDHRequest(checkAliveRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, CheckAliveRequestType.class, CheckAliveResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, false, false, nihiiOrg);
	}

}
