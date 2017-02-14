package be.business.connector.projects.common.services.tipsystem;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import be.ehealth.apb.gfddpp.services.tipsystem.CheckAliveRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.ObjectFactory;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedCheckAliveResponseType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedResponseType;
import be.ehealth.apb.gfddpp.services.tipsystem.SealedMessageRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.SimpleResponseType;

public class TipSystemServiceImpl implements TipSystemService {
	
	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(TipSystemServiceImpl.class);
	
	public static final String ENDPOINT_NAME = "endpoint.tipsystem";

	private static TipSystemService tipSystemService;

	private TipSystemServiceImpl() {
	}

	/**
	 * Gets the singleton instance of TipSystemServiceImpl.
	 * 
	 * @return singleton instance of TipSystemServiceImpl
	 */
	public static TipSystemService getInstance() {
		if (tipSystemService == null) {
			tipSystemService = new TipSystemServiceImpl();
		}
		return tipSystemService;
	}
	
	@Override
	public SimpleResponseType registerData(SealedMessageRequestType sealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<SealedMessageRequestType> jb = objectFactory.createRegisterDataRequest(sealedMessageRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedMessageRequestType.class, SimpleResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public SimpleResponseType updateData(SealedMessageRequestType sealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<SealedMessageRequestType> jb = objectFactory.createUpdateDataRequest(sealedMessageRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedMessageRequestType.class, SimpleResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public SimpleResponseType deleteData(SealedMessageRequestType sealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<SealedMessageRequestType> jb = objectFactory.createDeleteDataRequest(sealedMessageRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedMessageRequestType.class, SimpleResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public RoutedSealedResponseType getProductFilter(RoutedSealedRequestType routedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<RoutedSealedRequestType> jb = objectFactory.createGetProductFilterRequest(routedSealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, RoutedSealedRequestType.class, RoutedSealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public RoutedSealedResponseType getSystemServices(RoutedSealedRequestType routedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<RoutedSealedRequestType> jb = objectFactory.createGetSystemServicesRequest(routedSealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, RoutedSealedRequestType.class, RoutedSealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public RoutedSealedResponseType retrieveStatusMessages(RoutedSealedRequestType routedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<RoutedSealedRequestType> jb = objectFactory.createRetrieveStatusMessagesRequest(routedSealedRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, RoutedSealedRequestType.class, RoutedSealedResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public SimpleResponseType sendStatusMessages(SealedMessageRequestType sealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<SealedMessageRequestType> jb = objectFactory.createSendStatusMessagesRequest(sealedMessageRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, SealedMessageRequestType.class, SimpleResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

	@Override
	public RoutedCheckAliveResponseType checkAliveTIP(CheckAliveRequestType checkAliveRequestType, final String nihiiOrg) throws IntegrationModuleException {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<CheckAliveRequestType> jb = objectFactory.createCheckAliveTIPRequest(checkAliveRequestType);

		return GenericWebserviceCaller.callGenericWebservice(jb, CheckAliveRequestType.class, RoutedCheckAliveResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, false, false, false, nihiiOrg);
	}

}
