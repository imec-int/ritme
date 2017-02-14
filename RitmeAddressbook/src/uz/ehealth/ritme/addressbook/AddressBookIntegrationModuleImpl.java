package uz.ehealth.ritme.addressbook;

import be.business.connector.common.CommonIntegrationModule;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.module.AbstractIntegrationModule;
import be.business.connector.core.utils.Exceptionutils;
import be.business.connector.core.utils.I18nHelper;
import com.sun.xml.ws.client.ClientTransportException;
import org.apache.log4j.Logger;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsRequestType;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsResponseType;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchProfessionalsRequestType;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchProfessionalsResponseType;
import uz.ehealth.ritme.be.fgov.ehealth.commons.core.v2.StatusType;
import uz.ehealth.ritme.be.fgov.ehealth.commons.protocol.v2.StatusResponseType;

import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.UUID;


/**
 * Created by bdcuyp0 on 6-6-2016.
 */
public class AddressBookIntegrationModuleImpl extends AbstractIntegrationModule {

    private static final Logger LOG = Logger.getLogger(AddressBookIntegrationModuleImpl.class);
    private CommonIntegrationModule commonIntegrationModule;

    public AddressBookIntegrationModuleImpl(CommonIntegrationModule commonIntegrationModule) throws IntegrationModuleException {
        super();
        this.commonIntegrationModule = commonIntegrationModule;
        initEncryption(commonIntegrationModule.getPropertyHandler());
    }

    public AddressBookIntegrationModuleImpl() throws IntegrationModuleException {
        super();
    }


    public SearchProfessionalsResponseType searchProfessional(final String nihiiOrg, final String city, final String eMail, final String firstName, final String lastName, final String nihii, final String profession, final String zipCode, final String ssin) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // create request


            SearchProfessionalsRequestType request = new SearchProfessionalsRequestType();
            request.setSSIN(ssin);
            request.setCity(city);
            request.setEMail(eMail);
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setNIHII(nihii);
            request.setProfession(profession);
            request.setZipCode(zipCode);
            request.setMaxElements(100);
            request.setOffset(0);
            LOG.error(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).normalize().getClass().getCanonicalName());
            request.setIssueInstant(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).normalize());
            request.setId("ID-" + UUID.randomUUID().toString());

            // call sealed WS
            try {
                final SearchProfessionalsResponseType response = AddressBookService.getInstance().searchProfessional(request, nihiiOrg);
                checkStatus(response);
                return response;
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.addressbook"), cte);
            }
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    public SearchOrganizationsResponseType searchOrganisation(final String nihiiOrg, final String city, final String eMail, final String institutionName, final String nihii, final String quality, final String zipCode, final String cbe, final String ehp) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // create request


            SearchOrganizationsRequestType request = new SearchOrganizationsRequestType();
            request.setCity(city);
            request.setEMail(eMail);
            request.setInstitutionName(institutionName);
            request.setNIHII(nihii);
            request.setCBE(cbe);
            request.setEHP(ehp);
            request.setInstitutionType(quality);
            request.setZipCode(zipCode);
            request.setMaxElements(100);
            request.setOffset(0);
            LOG.error(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).normalize().getClass().getCanonicalName());

            request.setIssueInstant(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
            request.setId("ID-" + UUID.randomUUID().toString());

            // call sealed WS
            try {
                final SearchOrganizationsResponseType response = AddressBookService.getInstance().searchOrganization(request, nihiiOrg);
                checkStatus(response);
                return response;
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    private void checkStatus(StatusResponseType response) throws IntegrationModuleException {
        if (!"urn:be:fgov:ehealth:2.0:status:Success".equals(response.getStatus().getStatusCode().getValue())) {
            LOG.error("Error Status received : " + response.getStatus().getStatusCode().getValue());
            throw new IntegrationModuleException(getLocalisedMsg(response.getStatus()));
        }
    }

    private String getLocalisedMsg(StatusType status) {
        return status.getStatusMessage();
    }
}
