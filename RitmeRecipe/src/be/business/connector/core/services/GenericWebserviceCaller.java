package be.business.connector.core.services;

import be.apb.gfddpp.common.exceptions.GFDDPPException;
import be.apb.gfddpp.common.utils.JaxContextCentralizer;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.handlers.InsurabilityHandler;
import be.business.connector.core.handlers.LoggingHandler;
import be.business.connector.core.handlers.MustUnderstandHandler;
import be.business.connector.core.handlers.SoapFaultHandler;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.PropertyHandler;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.generic.session.GenericService;
import be.ehealth.technicalconnector.generic.session.GenericSessionServiceFactory;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.ws.domain.GenericRequest;
import be.ehealth.technicalconnector.ws.domain.GenericResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import uz.ehealth.ritme.outbound.hospital.SessionService;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.xml.soap.SOAPException;

public abstract class GenericWebserviceCaller {

	private static final Logger LOG = Logger.getLogger(GenericWebserviceCaller.class);

	public static <T extends Object> T callGenericWebservice(Object request, Class<T> responseType, String endpointName, Logger logger, String serviceName, boolean addLoggingHandler, boolean addSoapFaultHandler, boolean addMustUnderstandHandler, boolean addInsurabilityHandler, final String nihiiOrg) throws IntegrationModuleException {
		return callGenericWebservice(request, request.getClass(), responseType, endpointName, logger, serviceName, addLoggingHandler, addSoapFaultHandler, addMustUnderstandHandler, addInsurabilityHandler, nihiiOrg);
	}

	public static <T extends Object> T callGenericWebservice(Object request, Class<?> requestType, Class<T> responseType, String endpoint, Logger logger, String serviceName, boolean addLoggingHandler, boolean addSoapFaultHandler, boolean addMustUnderstandHandler, boolean addInsurabilityHandler, final String nihiiOrg) throws IntegrationModuleException {
		Object response = null;
		try {
            long startJaxbRequest = System.currentTimeMillis();
            String payload = JaxContextCentralizer.getInstance().toXml(requestType, request);
            long stopJaxbRequest = System.currentTimeMillis();
            LOG.error("Recipe jaxbRequest: " + (stopJaxbRequest - startJaxbRequest));
            GenericRequest genericRequest = new GenericRequest();
            long startPayload = System.currentTimeMillis();
            genericRequest.setPayload(payload);
            long stopPayload = System.currentTimeMillis();
            LOG.error("Recipe payload: " + (stopPayload - startPayload));
            genericRequest.setEndpoint(endpoint);
			//genericRequest.setSamlSecured();
            long startGetSession = System.currentTimeMillis();
            final SessionItem session = PluginManager.get("ritme.outbound.hospital.sessionmanager", SessionService.class).getSessionManager(nihiiOrg).getSession();
            long stopGetSession = System.currentTimeMillis();
            LOG.error("Recipe getSession: " + (stopGetSession - startGetSession));
            long startCredential = System.currentTimeMillis();
            genericRequest.setSamlSecured(session.getSAMLToken().getAssertion(), session.getHolderOfKeyCredential() );
            long stopCredential = System.currentTimeMillis();
            LOG.error("Recipe credential: " + (stopCredential - startCredential));
            if (addLoggingHandler) {
				LOG.info("LoggingHandler will be added");
				genericRequest.getAfterSecurityHandlerChain().add(new LoggingHandler());
			}
			if (addSoapFaultHandler) {
				LOG.info("SoapFaultHandler will be added");
				genericRequest.getAfterSecurityHandlerChain().add(new SoapFaultHandler());
			}
			if (addMustUnderstandHandler) {
				for (String property : PropertyHandler.getInstance().getMatchingProperties("connector.defaulthandlerchain.aftersecurity")) {
					if (property.contains("MustUnderstandHandler")) {
						LOG.info("MustUnderstandHandler will be added");
						genericRequest.getAfterSecurityHandlerChain().add(new MustUnderstandHandler());
					}
				}
			}
			if (addInsurabilityHandler) {
				LOG.info("InsurabilityHandler will be added");
				genericRequest.getAfterSecurityHandlerChain().add(new InsurabilityHandler());
			}	
			
			GenericService service = GenericSessionServiceFactory.getGenericService();
			LOG.info(serviceName + " called GenericWebservice to send message to service wiht endpoint:" + endpoint);
			long start = System.currentTimeMillis();
			GenericResponse resp = service.send(genericRequest);
			long stop = System.currentTimeMillis();
            LOG.error("Recipe send: " + (stop - start));
            LOG.info("GenericWebservice received a response from serice with endpoint:" + endpoint);
            long startJaxbResponse = System.currentTimeMillis();
            response = JaxContextCentralizer.getInstance().toObject(responseType, resp.asString());
            long stopJaxbResponse = System.currentTimeMillis();
            LOG.error("Recipe jaxbResponse: " + (stopJaxbResponse - startJaxbResponse));
        } catch (TechnicalConnectorException e) {
			LOG.error("TechnicalConnectorException generic webservice", e);
			String eHealthMessage = e.getLocalizedMessage();
			if (e.getCause() != null && StringUtils.isNotEmpty(e.getCause().getLocalizedMessage())) {
				eHealthMessage += " \nCause is: " + e.getCause().getLocalizedMessage();
			}
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.generic.webserive", new Object[] { serviceName, eHealthMessage }), e);
		} catch (ConnectorException e) {
			LOG.error("ConnectorException generic webservice", e);
			String eHealthMessage = e.getLocalizedMessage();
			if (e.getCause() != null && StringUtils.isNotEmpty(e.getCause().getLocalizedMessage())) {
				eHealthMessage += " \nCause is: " + e.getCause().getLocalizedMessage();
			}
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.generic.webserive", new Object[] { serviceName, eHealthMessage }), e);
		} catch (SOAPException e) {
			LOG.error("SOAPException generic webservice", e);
			String eHealthMessage = e.getLocalizedMessage();
			if (e.getCause() != null && StringUtils.isNotEmpty(e.getCause().getLocalizedMessage())) {
				eHealthMessage += " \nCause is: " + e.getCause().getLocalizedMessage();
			}
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.generic.webserive", new Object[] { serviceName, eHealthMessage }), e);
		} catch (GFDDPPException e) {
			LOG.error("Error generic webservice", e);
			throw new IntegrationModuleException(I18nHelper.getLabel("technical.connector.error.marshall.unmarshall"), e);
		}
		return responseType.cast(response);
	}

}
