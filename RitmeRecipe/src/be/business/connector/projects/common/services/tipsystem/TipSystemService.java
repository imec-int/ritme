package be.business.connector.projects.common.services.tipsystem;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.ehealth.apb.gfddpp.services.tipsystem.CheckAliveRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedCheckAliveResponseType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedResponseType;
import be.ehealth.apb.gfddpp.services.tipsystem.SealedMessageRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.SimpleResponseType;

public interface TipSystemService {
	  SimpleResponseType registerData(SealedMessageRequestType paramSealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  SimpleResponseType updateData(SealedMessageRequestType paramSealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  SimpleResponseType deleteData(SealedMessageRequestType paramSealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  RoutedSealedResponseType getProductFilter(RoutedSealedRequestType paramRoutedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  RoutedSealedResponseType getSystemServices(RoutedSealedRequestType paramRoutedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  RoutedSealedResponseType retrieveStatusMessages(RoutedSealedRequestType paramRoutedSealedRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  SimpleResponseType sendStatusMessages(SealedMessageRequestType paramSealedMessageRequestType, final String nihiiOrg) throws IntegrationModuleException;
	  RoutedCheckAliveResponseType checkAliveTIP(CheckAliveRequestType paramCheckAliveRequestType, final String nihiiOrg) throws IntegrationModuleException;
}
