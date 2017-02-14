package be.business.connector.recipe.executor.services;

import org.apache.log4j.Logger;

import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.fgov.ehealth.recipe.protocol.v3.executor.AliveCheckRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.AliveCheckResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.CreateFeedbackRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.CreateFeedbackResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.GetPrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.GetPrescriptionForExecutorResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.ListNotificationsRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.ListNotificationsResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsArchivedRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsArchivedResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsDeliveredResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsUnDeliveredRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.MarkAsUnDeliveredResponse;
import be.fgov.ehealth.recipe.protocol.v3.executor.RevokePrescriptionForExecutorRequest;
import be.fgov.ehealth.recipe.protocol.v3.executor.RevokePrescriptionForExecutorResponse;

public class RecipeExecutorServiceV3Impl implements RecipeExecutorServiceV3 {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(RecipeExecutorServiceV3Impl.class);

	private static final String ENDPOINT_NAME = "endpoint.executor.v3";

	private static RecipeExecutorServiceV3 recipeExecutorService;

	private RecipeExecutorServiceV3Impl() {
	}

	/**
	 * Gets the singleton instance of RecipeExecutorServiceV3Impl.
	 * 
	 * @return singleton instance of RecipeExecutorServiceV3Impl
	 */
	public static RecipeExecutorServiceV3 getInstance() {
		if (recipeExecutorService == null) {
			recipeExecutorService = new RecipeExecutorServiceV3Impl();
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
