package be.business.connector.recipe.prescriber.services;

import org.apache.log4j.Logger;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.AliveCheckRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.AliveCheckResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.CreatePrescriptionRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.CreatePrescriptionResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.GetPrescriptionForPrescriberRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.GetPrescriptionForPrescriberResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.ListFeedbacksRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.ListFeedbacksResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.ListOpenPrescriptionsRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.ListOpenPrescriptionsResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.RevokePrescriptionRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.RevokePrescriptionResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.SendNotificationRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.SendNotificationResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.UpdateFeedbackFlagRequest;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.UpdateFeedbackFlagResponse;

public class RecipePrescriberServiceImpl implements RecipePrescriberService {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(RecipePrescriberServiceImpl.class);

	private static final String ENDPOINT_NAME = "endpoint.prescriber";

	private static RecipePrescriberService recipePrescriberService;

	private RecipePrescriberServiceImpl() {
	}

	/**
	 * Gets the singleton instance of RecipePrescriberServiceImpl.
	 * 
	 * @return singleton instance of RecipePrescriberServiceImpl
	 */
	public static RecipePrescriberService getInstance() {
		if (recipePrescriberService == null) {
			recipePrescriberService = new RecipePrescriberServiceImpl();
		}
		return recipePrescriberService;
	}

	@Override
	public AliveCheckResponse aliveCheck(AliveCheckRequest aliveCheckRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(aliveCheckRequest, AliveCheckResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public CreatePrescriptionResponse createPrescription(CreatePrescriptionRequest createPrescriptionRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(createPrescriptionRequest, CreatePrescriptionResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public RevokePrescriptionResponse revokePrescription(RevokePrescriptionRequest revokePrescriptionRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(revokePrescriptionRequest, RevokePrescriptionResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public GetPrescriptionForPrescriberResponse getPrescriptionForPrescriber(GetPrescriptionForPrescriberRequest getPrescriptionForPrescriberRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(getPrescriptionForPrescriberRequest, GetPrescriptionForPrescriberResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public ListOpenPrescriptionsResponse listOpenPrescriptions(ListOpenPrescriptionsRequest listOpenPrescriptionsRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(listOpenPrescriptionsRequest, ListOpenPrescriptionsResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public SendNotificationResponse sendNotification(SendNotificationRequest sendNotificationRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(sendNotificationRequest, SendNotificationResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public UpdateFeedbackFlagResponse updateFeedbackFlag(UpdateFeedbackFlagRequest updateFeedbackFlagRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(updateFeedbackFlagRequest, UpdateFeedbackFlagResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

	@Override
	public ListFeedbacksResponse listFeedbacks(ListFeedbacksRequest listFeedbacksRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(listFeedbacksRequest, ListFeedbacksResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
	}

}
