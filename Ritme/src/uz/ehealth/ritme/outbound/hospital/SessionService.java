package uz.ehealth.ritme.outbound.hospital;


import be.ehealth.technicalconnector.session.SessionManager;

/**
 * Created by bdcuyp0 on 25-6-2015.
 */
public interface SessionService {

    public SessionManager getSessionManager(String nihiiOrg);

}
