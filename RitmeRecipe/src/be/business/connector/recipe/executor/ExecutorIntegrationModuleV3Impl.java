package be.business.connector.recipe.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;

import be.business.connector.common.CommonIntegrationModule;
import be.business.connector.common.RequestorProvider;
import be.business.connector.core.domain.IdentifierTypes;
import be.business.connector.core.domain.KgssIdentifierType;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.handlers.InsurabilityHandler;
import be.business.connector.core.module.AbstractIntegrationModule;
import be.business.connector.core.technical.connector.utils.Crypto;
import be.business.connector.core.utils.ETKHelper;
import be.business.connector.core.utils.Exceptionutils;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.IOUtils;
import be.business.connector.core.utils.MarshallerHelper;
import be.business.connector.core.utils.PropertyHandler;
import be.business.connector.recipe.executor.domain.GetPrescriptionForExecutorResult;
import be.business.connector.recipe.executor.services.RecipeExecutorServiceV3Impl;
import be.business.connector.recipe.utils.KmehrHelper;
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionToken;
import be.fgov.ehealth.recipe.protocol.v3.executor.CreateFeedbackRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.ExecutorServiceAdministrativeInformationType;
import be.fgov.ehealth.recipe.protocol.v3.executor.GetPrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.GetPrescriptionForExecutorResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.IdentifierType;
import be.fgov.ehealth.recipe.protocol.v3.executor.ListNotificationsRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.ListNotificationsResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.LocalisedString;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsArchivedRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsUnDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.ResponseType;
import be.fgov.ehealth.recipe.protocol.v3.executor.RevokePrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.SecuredContentType;
import be.fgov.ehealth.recipe.protocol.v3.executor.StatusType;
import be.recipe.client.services.executor.CreateFeedbackParam;
import be.recipe.client.services.executor.GetPrescriptionForExecutorParam;
import be.recipe.client.services.executor.GetPrescriptionForExecutorResultSealed;
import be.recipe.client.services.executor.ListNotificationsItem;
import be.recipe.client.services.executor.ListNotificationsParam;
import be.recipe.client.services.executor.ListNotificationsResult;
import be.recipe.client.services.executor.MarkAsArchivedParam;
import be.recipe.client.services.executor.MarkAsDeliveredParam;
import be.recipe.client.services.executor.MarkAsUndeliveredParam;
import be.recipe.client.services.executor.RevokePrescriptionParam;

import com.sun.xml.ws.client.ClientTransportException;

public class ExecutorIntegrationModuleV3Impl extends AbstractIntegrationModule implements ExecutorIntegrationModule {
	private final static Logger LOG = Logger.getLogger(ExecutorIntegrationModuleV3Impl.class);
	private Map<String, GetPrescriptionForExecutorResult> prescriptionCache = new HashMap<String, GetPrescriptionForExecutorResult>();
	private RequestorProvider requestorIntegrationModule;
	private KmehrHelper kmehrHelper;
	
	public ExecutorIntegrationModuleV3Impl(RequestorProvider requestorIntegrationModule, CommonIntegrationModule commonIntegrationModule, final String nihiiOrg) throws IntegrationModuleException {
		super();
		kmehrHelper = new KmehrHelper(commonIntegrationModule.getPropertyHandler().getProperties());
		this.requestorIntegrationModule = requestorIntegrationModule;
		initEncryption(commonIntegrationModule.getPropertyHandler());
	}

	public ExecutorIntegrationModuleV3Impl() throws IntegrationModuleException {
		super();
		if(PropertyHandler.getInstance() != null){
			kmehrHelper = new KmehrHelper(PropertyHandler.getInstance().getProperties());
		}else{
			//this is for the mock!
			kmehrHelper = new KmehrHelper(new PropertyHandler().getProperties());
		}
		
	}

	@Profiled(logFailuresSeparately = true, tag = "ExecutorIntegrationModule#getPrescription")
	@Override
	public GetPrescriptionForExecutorResult getPrescription(String rid, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		InsurabilityHandler.setInsurability(null);
		InsurabilityHandler.setMessageId(null);

		try {
			MarshallerHelper<GetPrescriptionForExecutorResultSealed, GetPrescriptionForExecutorParam> marshaller = new MarshallerHelper<GetPrescriptionForExecutorResultSealed, GetPrescriptionForExecutorParam>(GetPrescriptionForExecutorResultSealed.class, GetPrescriptionForExecutorParam.class);
			GetPrescriptionForExecutorRequest request = createGetPrescriptionRequest(rid, marshaller, nihiiOrg);
			                 GetPrescriptionForExecutorResponse response = null;

			try {
				response = RecipeExecutorServiceV3Impl.getInstance().getPrescriptionForExecutor(request, nihiiOrg);
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}
			checkStatus(response);

			GetPrescriptionForExecutorResultSealed result = marshaller.unsealWithSymmKey(response.getSecuredGetPrescriptionForExecutorResponse().getSecuredContent(), getSymmKey(nihiiOrg));

			KeyResult key =  getKeyFromKgss(result.getEncryptionKeyId(), getEtkHelper(nihiiOrg).getEtks(KgssIdentifierType.NIHII_PHARMACY, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg))).get(0).getEncoded(),nihiiOrg );

			be.business.connector.recipe.executor.domain.GetPrescriptionForExecutorResult result2 = new be.business.connector.recipe.executor.domain.GetPrescriptionForExecutorResult(result);
			result2.setSealedContent(result.getPrescription());
			byte[] unsealedPrescription = unsealPrescriptionForUnknown(key, result.getPrescription(), nihiiOrg);
			unsealedPrescription = IOUtils.decompress(unsealedPrescription);
			result2.setPrescription(unsealedPrescription);
			result2.setEncryptionKey(key.getSecretKey().getEncoded());
			result2.setInsurabilityResponse(InsurabilityHandler.getInsurability());
			result2.setMessageId(InsurabilityHandler.getMessageId());
			prescriptionCache.put(rid, result2);

			return result2;
		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
		return null;
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#markAsArchived")
	@Override
	public void markAsArchived(String rid, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			MarshallerHelper<Object, MarkAsArchivedParam> helper = new MarshallerHelper<Object, MarkAsArchivedParam>(Object.class, MarkAsArchivedParam.class);

			// get Recipe ETK
			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

			// create param
			MarkAsArchivedParam param = new MarkAsArchivedParam();
			param.setRid(rid);
			param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

			// create request
			MarkAsArchivedRequest request = new MarkAsArchivedRequest();
			request.setSecuredMarkAsArchivedRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
			request.setAdministrativeInformation(getAdministrativeInfo(rid));

			// call WS
			try {
				checkStatus(RecipeExecutorServiceV3Impl.getInstance().markAsArchived(request, nihiiOrg));
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}
		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#markAsDelivered")
	@Override
	public void markAsDelivered(String rid, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			MarshallerHelper<Object, MarkAsDeliveredParam> helper = new MarshallerHelper<Object, MarkAsDeliveredParam>(Object.class, MarkAsDeliveredParam.class);
			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

			MarkAsDeliveredParam param = new MarkAsDeliveredParam();
			param.setRid(rid);
			param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

			MarkAsDeliveredRequest request = new MarkAsDeliveredRequest();
			request.setSecuredMarkAsDeliveredRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
			request.setAdministrativeInformation(getAdministrativeInfo(rid));

			try {
				checkStatus(RecipeExecutorServiceV3Impl.getInstance().markAsDelivered(request, nihiiOrg));
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}
		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#markAsUndelivered")
	@Override
	public void markAsUndelivered(String rid, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			MarshallerHelper<Object, MarkAsUndeliveredParam> helper = new MarshallerHelper<Object, MarkAsUndeliveredParam>(Object.class, MarkAsUndeliveredParam.class);
			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

			MarkAsUndeliveredParam param = new MarkAsUndeliveredParam();
			param.setRid(rid);
			param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

			MarkAsUnDeliveredRequest request = new MarkAsUnDeliveredRequest();
			request.setSecuredMarkAsUnDeliveredRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
			request.setAdministrativeInformation(getAdministrativeInfo(rid));

			try {
				checkStatus(RecipeExecutorServiceV3Impl.getInstance().markAsUnDelivered(request, nihiiOrg));
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}
		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#revokePrescription")
	@Override
	public void revokePrescription(String rid, String reason, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			MarshallerHelper<Object, RevokePrescriptionParam> helper = new MarshallerHelper<Object, RevokePrescriptionParam>(Object.class, RevokePrescriptionParam.class);
			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

			RevokePrescriptionParam param = new RevokePrescriptionParam();
			param.setRid(rid);
			param.setReason(reason);
			param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

			RevokePrescriptionForExecutorRequest request = new RevokePrescriptionForExecutorRequest();
			request.setAdministrativeInformation(getAdministrativeInfo(rid));
			request.setSecuredRevokePrescriptionRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));

			try {
				checkStatus(RecipeExecutorServiceV3Impl.getInstance().revokePrescriptionForExecutor(request, nihiiOrg));
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}

		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#listNotifications")
	@Override
	public List<ListNotificationsItem> listNotifications(boolean readFlag, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			MarshallerHelper<ListNotificationsResult, ListNotificationsParam> helper = new MarshallerHelper<ListNotificationsResult, ListNotificationsParam>(ListNotificationsResult.class, ListNotificationsParam.class);
			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

			ListNotificationsParam param = new ListNotificationsParam();
			param.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
			param.setReadFlag(readFlag);
			param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

			ListNotificationsRequest request = new ListNotificationsRequest();
			request.setSecuredListNotificationsRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));

			ListNotificationsResponse response = null;
			try {
				response = RecipeExecutorServiceV3Impl.getInstance().listNotifications(request, nihiiOrg);
			} catch (ClientTransportException cte) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
			}
			checkStatus(response);

			// unseal WS response
			List<ListNotificationsItem> items = helper.unsealWithSymmKey(response.getSecuredListNotificationsResponse().getSecuredContent(), getSymmKey(nihiiOrg)).getNotifications();

			for (int i = 0; i < items.size(); i++) {
				be.business.connector.recipe.executor.domain.ListNotificationsItem item = new be.business.connector.recipe.executor.domain.ListNotificationsItem(items.get(i));
				byte[] content = item.getContent();
				try {
					content = unsealNotification(content, nihiiOrg);
					content = IOUtils.decompress(content);
					item.setContent(content);
				} catch (Throwable t) {
					item.setLinkedException(t);
				}
				items.set(i, item);
			}
			return items;

		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
		return null;
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#createFeedback")
	@Override
	public void createFeedback(long prescriberId, String rid, byte[] feedbackText, final String nihiiOrg) throws IntegrationModuleException {
		// assertValidSession();
		try {
			getKmehrHelper().assertValidFeedback(feedbackText);
			MarshallerHelper<Object, CreateFeedbackParam> helper = new MarshallerHelper<Object, CreateFeedbackParam>(Object.class, CreateFeedbackParam.class);

			final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();
			List<EncryptionToken> etkRecipients = getEtkHelper(nihiiOrg).getEtks(KgssIdentifierType.NIHII, prescriberId);

			for (int i = 0; i < etkRecipients.size(); i++) {
				EncryptionToken etkRecipient = etkRecipients.get(i);

				byte[] message = IOUtils.compress(feedbackText);
				message = sealFeedback(etkRecipient, message, nihiiOrg);

				CreateFeedbackParam param = new CreateFeedbackParam();
				param.setContent(message);
				param.setPrescriberId(prescriberId);
				param.setRid(rid);
				param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

				CreateFeedbackRequest request = new CreateFeedbackRequest();
				request.setSecuredCreateFeedbackRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
				ExecutorServiceAdministrativeInformationType info = getAdministrativeInfo(rid);
				info.setPrescriberIdentifier(createIdentifierType(ETKHelper.longToString(prescriberId, 11), IdentifierTypes.SSIN.name()));
				request.setAdministrativeInformation(info);

				try {
					checkStatus(RecipeExecutorServiceV3Impl.getInstance().createFeedback(request, nihiiOrg));
				} catch (ClientTransportException cte) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.executor"), cte);
				}
			}
		} catch (Throwable t) {
			Exceptionutils.errorHandler(t);
		}
	}

	private GetPrescriptionForExecutorRequest createGetPrescriptionRequest(String rid, MarshallerHelper<GetPrescriptionForExecutorResultSealed, GetPrescriptionForExecutorParam> marshaller, final String nihiiOrg) throws IntegrationModuleException {
		GetPrescriptionForExecutorParam param = new GetPrescriptionForExecutorParam();
		param.setRid(rid);
		param.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
		param.setExecutorId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

		GetPrescriptionForExecutorRequest request = new GetPrescriptionForExecutorRequest();
		request.setDisablePatientInsurabilityCheckParam(Boolean.parseBoolean(getPropertyHandler().getProperty("patient.insurability.disable"))); // will be false if
																																					// not defined in
																																					// property file
		request.setSecuredGetPrescriptionForExecutorRequest(createSecuredContentTypeV2(sealRequest(getEtkHelper(nihiiOrg).getRecipe_ETK().get(0), marshaller.toXMLByteArray(param), nihiiOrg)));
		return request;
	}

	private SecuredContentType createSecuredContentType(byte[] content) {
		SecuredContentType secured = new SecuredContentType();
		secured.setSecuredContent(content);
		return secured;
	}

	private SecuredContentType createSecuredContentTypeV2(byte[] content) {
		SecuredContentType secured = new SecuredContentType();
		secured.setSecuredContent(content);
		return secured;
	}

	private void checkStatus(ResponseType response) throws IntegrationModuleException {
		if (!EHEALTH_SUCCESS_CODE_100.equals(response.getStatus().getCode()) && !EHEALTH_SUCCESS_CODE_200.equals(response.getStatus().getCode())) {
			LOG.error("Error Status received : " + response.getStatus().getCode());
			throw new IntegrationModuleException(getLocalisedMsg(response.getStatus()));
		}
	}

	private String getLocalisedMsg(StatusType status) {
		final String locale = IntegrationModuleException.getUserLocale();
		for (LocalisedString msg : status.getMessages()) {
			if (msg.getLang() != null && locale.equalsIgnoreCase(msg.getLang().value())) {
				return msg.getValue();
			}
		}
		if (status.getMessages().size() > 0) {
			return status.getMessages().get(0).getValue();
		}
		return status.getCode();
	}

	private ExecutorServiceAdministrativeInformationType getAdministrativeInfo(String rid) {
		GetPrescriptionForExecutorResult prescription = prescriptionCache.get(rid);
		ExecutorServiceAdministrativeInformationType info = new ExecutorServiceAdministrativeInformationType();
		if (prescription != null) {
			info.setPatientIdentifier(createIdentifierType(prescription.getPatientId(), IdentifierTypes.SSIN.name()));
			info.setPrescriberIdentifier(createIdentifierType(ETKHelper.longToString(prescription.getPrescriberId(), 11), IdentifierTypes.NIHII11.name()));
		} else {
			info.setPatientIdentifier(createIdentifierType("99999999999", IdentifierTypes.SSIN.name()));
			info.setPrescriberIdentifier(createIdentifierType("10998018001", IdentifierTypes.NIHII11.name()));
		}
		return info;
	}

	private IdentifierType createIdentifierType(final String id, final String type) {
		IdentifierType ident = new IdentifierType();
		ident.setId(id);
		ident.setType(type);
		return ident;
	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#unsealNotification")
	protected byte[] unsealNotification(byte[] message, final String nihiiOrg) throws IntegrationModuleException {
		return unsealNotiffeed(message, nihiiOrg);

	}

	@Profiled(logFailuresSeparately = true, tag = "0.ExecutorIntegrationModule#sealFeedback")
	protected byte[] sealFeedback(EncryptionToken paramEncryptionToken, byte[] paramArrayOfByte, final String nihiiOrg) throws IntegrationModuleException {
		return Crypto.seal(paramEncryptionToken, paramArrayOfByte, nihiiOrg);
	}
	

	private KmehrHelper getKmehrHelper() {
		return kmehrHelper;
	}

}
