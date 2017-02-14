package be.business.connector.recipe.executor.services;

import org.apache.log4j.Logger;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import be.fgov.ehealth.recipe.protocol.v2.AliveCheckRequest;
import be.fgov.ehealth.recipe.protocol.v2.AliveCheckResponse;
import be.fgov.ehealth.recipe.protocol.v2.CreateFeedbackRequest;
import be.fgov.ehealth.recipe.protocol.v2.CreateFeedbackResponse;
import be.fgov.ehealth.recipe.protocol.v2.GetPrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v2.GetPrescriptionForExecutorResponse;
import be.fgov.ehealth.recipe.protocol.v2.ListNotificationsRequest;
import be.fgov.ehealth.recipe.protocol.v2.ListNotificationsResponse;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsArchivedRequest;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsArchivedResponse;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsDeliveredResponse;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsUnDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v2.MarkAsUnDeliveredResponse;
import be.fgov.ehealth.recipe.protocol.v2.RevokePrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v2.RevokePrescriptionForExecutorResponse;

public class RecipeExecutorServiceImpl implements RecipeExecutorService {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(RecipeExecutorServiceImpl.class);

	private static final String ENDPOINT_NAME = "endpoint.executor";

	private static RecipeExecutorService recipeExecutorService;

	private RecipeExecutorServiceImpl() {
	}

	/**
	 * Gets the singleton instance of RecipeExecutorServiceImpl.
	 * 
	 * @return singleton instance of RecipeExecutorServiceImpl
	 */
	public static RecipeExecutorService getInstance() {
		if (recipeExecutorService == null) {
			recipeExecutorService = new RecipeExecutorServiceImpl();
		}
		return recipeExecutorService;
	}

	@Override
	public RevokePrescriptionForExecutorResponse revokePrescriptionForExecutor(RevokePrescriptionForExecutorRequest revokePrescriptionForExecutorRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(revokePrescriptionForExecutorRequest, RevokePrescriptionForExecutorResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public AliveCheckResponse aliveCheck(AliveCheckRequest aliveCheckRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(aliveCheckRequest, AliveCheckResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public CreateFeedbackResponse createFeedback(CreateFeedbackRequest createFeedbackRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(createFeedbackRequest, CreateFeedbackResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public GetPrescriptionForExecutorResponse getPrescriptionForExecutor(GetPrescriptionForExecutorRequest getPrescriptionForExecutorRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(getPrescriptionForExecutorRequest, GetPrescriptionForExecutorResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public MarkAsArchivedResponse markAsArchived(MarkAsArchivedRequest markAsArchivedRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(markAsArchivedRequest, MarkAsArchivedResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public MarkAsDeliveredResponse markAsDelivered(MarkAsDeliveredRequest markAsDeliveredRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(markAsDeliveredRequest, MarkAsDeliveredResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public MarkAsUnDeliveredResponse markAsUnDelivered(MarkAsUnDeliveredRequest markAsUnDeliveredRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(markAsUnDeliveredRequest, MarkAsUnDeliveredResponse.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

	@Override
	public ListNotificationsResponse listNotifications(ListNotificationsRequest listNotificationsRequest, final String nihiiOrg) throws IntegrationModuleException {
		return GenericWebserviceCaller.callGenericWebservice(listNotificationsRequest, ListNotificationsResponse.class,EndpointResolver.getEndpointUrlString( ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, true, nihiiOrg);
	}

}
