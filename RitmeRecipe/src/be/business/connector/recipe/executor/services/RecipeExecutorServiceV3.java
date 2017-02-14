package be.business.connector.recipe.executor.services;

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


public interface RecipeExecutorServiceV3 {
	RevokePrescriptionForExecutorResponse revokePrescriptionForExecutor(RevokePrescriptionForExecutorRequest paramRevokePrescriptionForExecutorRequest, final String nihiiOrg) throws IntegrationModuleException;

	AliveCheckResponse aliveCheck(AliveCheckRequest paramAliveCheckRequest, final String nihiiOrg) throws IntegrationModuleException;

	CreateFeedbackResponse createFeedback(CreateFeedbackRequest paramCreateFeedbackRequest, final String nihiiOrg) throws IntegrationModuleException;

	GetPrescriptionForExecutorResponse getPrescriptionForExecutor(GetPrescriptionForExecutorRequest paramGetPrescriptionForExecutorRequest, final String nihiiOrg) throws IntegrationModuleException;

	MarkAsArchivedResponse markAsArchived(MarkAsArchivedRequest paramMarkAsArchivedRequest, final String nihiiOrg) throws IntegrationModuleException;

	MarkAsDeliveredResponse markAsDelivered(MarkAsDeliveredRequest paramMarkAsDeliveredRequest, final String nihiiOrg) throws IntegrationModuleException;

	MarkAsUnDeliveredResponse markAsUnDelivered(MarkAsUnDeliveredRequest paramMarkAsUnDeliveredRequest, final String nihiiOrg) throws IntegrationModuleException;

	ListNotificationsResponse listNotifications(ListNotificationsRequest paramListNotificationsRequest, final String nihiiOrg) throws IntegrationModuleException;
}
