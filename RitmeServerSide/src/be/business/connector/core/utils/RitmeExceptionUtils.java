package be.business.connector.core.utils;

import be.business.connector.core.exceptions.IntegrationModuleEhealthException;
import be.business.connector.core.exceptions.IntegrationModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bdcuyp0 on 11-7-2016.
 */
public class RitmeExceptionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyHandler.class);

    public static void errorHandler(Throwable t) throws IntegrationModuleException, IntegrationModuleEhealthException {
        if (t instanceof IntegrationModuleEhealthException) {
            LOG.error("", t);
            throw (IntegrationModuleEhealthException) t;
        } else if (t.getCause() instanceof IntegrationModuleEhealthException) {
            LOG.error("", t);
            throw (IntegrationModuleEhealthException) t.getCause();
        } else if (t instanceof IntegrationModuleException) {
            LOG.error("", t);
            throw (IntegrationModuleException) t;
        } else {
            LOG.error("", t);
            throw new IntegrationModuleException(I18nHelper.getLabel("error.technical"), t);
        }
    }

    public static void errorHandler(Throwable t, String errorMsg) throws IntegrationModuleException, IntegrationModuleEhealthException {
        if (t instanceof IntegrationModuleEhealthException) {
            LOG.error("", t);
            throw (IntegrationModuleEhealthException) t;
        } else if (t.getCause() instanceof IntegrationModuleEhealthException) {
            LOG.error("", t);
            throw (IntegrationModuleEhealthException) t.getCause();
        } else if (t instanceof IntegrationModuleException) {
            LOG.error("", t);
            throw (IntegrationModuleException) t;
        } else {
            LOG.error("", t);
            throw new IntegrationModuleException(I18nHelper.getLabel(errorMsg), t);
        }
    }
}
