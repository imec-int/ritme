package uz.ehealth.ritme.vitalink;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.ConfigValidator;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.session.SessionManager;
import be.smals.safe.connector.IVitalinkServiceConcurrency;
import be.smals.safe.connector.VitalinkServiceConcurrency;
import uz.ehealth.ritme.outbound.hospital.SessionService;
import uz.ehealth.ritme.plugins.PluginManager;

public class RitmeVitalink {
    //private VitalinkServiceConcurrency vitalink = new VitalinkServiceConcurrency();
    //static because this is a cache
    private ConfigValidator config = ConfigFactory.getConfigValidatorFor("sessionmanager.samlattribute", "sessionmanager.samlattributedesignator");


    /**
     * <p>
     * Initializes the Session Management component with a valid eHealth STS Token
     * </p>
     * <p>
     * This example uses a standard Doctor profile (with EID) for demonstration purposes.
     * </p>
     *
     * @throws TechnicalConnectorException
     */
    public SessionItem initializeSessionManagementForOrganisation(String nihiiOrg) throws TechnicalConnectorException {


        SessionManager sessionManager = PluginManager.get("ritme.outbound.hospital.sessionmanager", SessionService.class).getSessionManager(nihiiOrg);


        if (!sessionManager.hasValidSession()) {
            /*******************************
             * Request eHealth STS Token
             * In case only eID is available use the "createSessionEidOnly()" method
             *******************************/

            //Session.getInstance().createSessionEidOnly();
            String pwd = config.getProperty("KEYSTORE_PASSWORD", null);
            if (pwd == null) {
                System.getProperty("KEYSTORE_PASSWORD", null);
            }
            return sessionManager.createFallbackSession(pwd, pwd);
        } else {
            return sessionManager.getSession();
        }
    }


    public IVitalinkServiceConcurrency getVitalink() {
        return new VitalinkServiceConcurrency();
    }

}
