package uz.ehealth.ritme.outbound.hospital;

import be.ehealth.technicalconnector.session.SessionManager;
import be.ehealth.technicalconnector.session.impl.SessionManagerImpl;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class DefaultSessionService implements SessionService {
    @Override
    public SessionManager getSessionManager(final String nihiiOrg) {
        return SessionManagerImpl.getInstance();
    }
}
