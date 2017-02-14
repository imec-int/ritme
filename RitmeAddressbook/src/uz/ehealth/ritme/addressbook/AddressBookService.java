package uz.ehealth.ritme.addressbook;

import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.services.GenericWebserviceCaller;
import be.business.connector.projects.common.utils.EndpointResolver;
import org.apache.log4j.Logger;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.*;

import javax.xml.bind.JAXBElement;


/**
 * Created by bdcuyp0 on 6-6-2016.
 */
public class AddressBookService {

    public static final Logger LOG = Logger.getLogger(AddressBookService.class);
    public static final String ENDPOINT_NAME = "endpoint.addressbook";

    public SearchProfessionalsResponseType searchProfessional(SearchProfessionalsRequestType searchProfessionalRequest, final String nihiiOrg) throws IntegrationModuleException {
        ObjectFactory factory = new ObjectFactory();
        final JAXBElement<SearchProfessionalsRequestType> wrappedRequest = factory.createSearchProfessionalsRequest(searchProfessionalRequest);
        return GenericWebserviceCaller.callGenericWebservice(wrappedRequest, SearchProfessionalsResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
    }

    public SearchOrganizationsResponseType searchOrganization(SearchOrganizationsRequestType searchOrganizationsRequest, final String nihiiOrg) throws IntegrationModuleException {
        ObjectFactory factory = new ObjectFactory();
        final JAXBElement<SearchOrganizationsRequestType> wrappedRequest = factory.createSearchOrganizationsRequest(searchOrganizationsRequest);
        return GenericWebserviceCaller.callGenericWebservice(wrappedRequest, SearchOrganizationsResponseType.class, EndpointResolver.getEndpointUrlString(ENDPOINT_NAME), LOG, getClass().getName(), true, true, true, false, nihiiOrg);
    }

    private AddressBookService() {
    }

    private static class AddressBookServiceHolder {
        private static final AddressBookService ADDRESS_BOOK_SERVICE = new AddressBookService();
    }

    /**
     * Gets the singleton instance of RecipePrescriberServiceImpl.
     *
     * @return singleton instance of RecipePrescriberServiceImpl
     */
    public static AddressBookService getInstance() {
        return AddressBookServiceHolder.ADDRESS_BOOK_SERVICE;
    }
}
