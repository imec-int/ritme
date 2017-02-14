package be.business.connector.recipe.prescriber.services;

import be.business.connector.core.exceptions.IntegrationModuleException;
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

public interface RecipePrescriberService {
	AliveCheckResponse aliveCheck(AliveCheckRequest paramAliveCheckRequest, final String nihiiOrg) throws IntegrationModuleException;

	CreatePrescriptionResponse createPrescription(CreatePrescriptionRequest paramCreatePrescriptionRequest, final String nihiiOrg) throws IntegrationModuleException;

	RevokePrescriptionResponse revokePrescription(RevokePrescriptionRequest paramRevokePrescriptionRequest, final String nihiiOrg) throws IntegrationModuleException;

	GetPrescriptionForPrescriberResponse getPrescriptionForPrescriber(GetPrescriptionForPrescriberRequest paramGetPrescriptionForPrescriberRequest, final String nihiiOrg) throws IntegrationModuleException;

	ListOpenPrescriptionsResponse listOpenPrescriptions(ListOpenPrescriptionsRequest paramListOpenPrescriptionsRequest, final String nihiiOrg) throws IntegrationModuleException;

	SendNotificationResponse sendNotification(SendNotificationRequest paramSendNotificationRequest, final String nihiiOrg) throws IntegrationModuleException;

	UpdateFeedbackFlagResponse updateFeedbackFlag(UpdateFeedbackFlagRequest paramUpdateFeedbackFlagRequest, final String nihiiOrg) throws IntegrationModuleException;

	ListFeedbacksResponse listFeedbacks(ListFeedbacksRequest paramListFeedbacksRequest, final String nihiiOrg) throws IntegrationModuleException;
}
