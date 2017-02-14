package be.business.connector.recipe.prescriber;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.recipe.client.services.prescriber.GetPrescriptionForPrescriberResult;
import be.recipe.client.services.prescriber.ListFeedbackItem;

import java.util.List;

public interface PrescriberIntegrationModule {
	String createPrescription(boolean feedbackRequested, long patientId, byte[] prescription, String prescriptionType, final String nihiiOrg) throws IntegrationModuleException;

    void revokePrescription(String rid, String reason, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException;

    GetPrescriptionForPrescriberResult getPrescription(String rid, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException;

	List<String> listOpenPrescription(String patientId, final String nihiiOrg, final Long prescriberId) throws IntegrationModuleException;

        List<String> listOpenPrescription(final String nihiiOrg) throws IntegrationModuleException;
	
	void sendNotification(byte[] notificationText, String patientId, long executorId, final String nihiiOrg) throws IntegrationModuleException;

    void updateFeedbackFlag(String rid, boolean feedbackAllowed, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException;

    List<ListFeedbackItem> listFeedback(boolean readFlag, final String nihiiOrg) throws IntegrationModuleException;
	
	void ping(final String nihiiOrg) throws IntegrationModuleException;
	
	void setPersonalPassword(String personalPassword, final String nihiiOrg) throws IntegrationModuleException;
	
	void prepareCreatePrescription(String patientId, String prescriptionType, final String nihiiOrg) throws IntegrationModuleException;
}
