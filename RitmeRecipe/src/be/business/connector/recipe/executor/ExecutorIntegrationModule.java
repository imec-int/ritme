package be.business.connector.recipe.executor;

import java.util.List;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.recipe.executor.domain.GetPrescriptionForExecutorResult;
import be.recipe.client.services.executor.ListNotificationsItem;


public interface ExecutorIntegrationModule{
	GetPrescriptionForExecutorResult getPrescription(String rid, final String nihiiOrg) throws IntegrationModuleException;
	
	void markAsDelivered(String rid, final String nihiiOrg) throws IntegrationModuleException;
	
	void markAsArchived(String rid, final String nihiiOrg) throws IntegrationModuleException;
	
	void markAsUndelivered(String rid, final String nihiiOrg) throws IntegrationModuleException;
	
	void revokePrescription(String rid, String reason, final String nihiiOrg) throws IntegrationModuleException;
	
	List<ListNotificationsItem> listNotifications(boolean readFlag, final String nihiiOrg) throws IntegrationModuleException;
	
	void createFeedback(long prescriberId, String rid, byte[] feedbackText, final String nihiiOrg) throws IntegrationModuleException;
}
