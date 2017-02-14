/**
 * Copyright (C) 2010 Recip-e
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.business.connector.core.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class IntegrationModuleException extends Exception {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationModuleException.class);

    private static final long serialVersionUID = 1L;


    public IntegrationModuleException(String message, Throwable cause) {
        super(message, cause);
        LOG.error(message, this);
    }

    public IntegrationModuleException(String message) {
        super(message);
        LOG.error(message, this);
    }

    public IntegrationModuleException(Throwable cause) {
        super(cause);
        LOG.error("IntegrationModuleException", cause);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        final String locale = getUserLocale();

        // Prescriber
        Throwable cause = getCause();

        String entry = getCustomMessageString(locale, cause);
        if (entry != null) {
            return entry;
        }


        // Local error
        return super.getMessage();
    }

    private String getCustomMessageString(final String locale, final Throwable cause) {
        return null;
    }

    /**
     * Gets the user locale.
     *
     * @return the user locale
     */
    public static String getUserLocale() {
        String locale = Locale.getDefault().getLanguage();

        if (!locale.equalsIgnoreCase("nl")
                && !locale.equalsIgnoreCase("fr")
                && !locale.equalsIgnoreCase("en")) {
            Locale.setDefault(Locale.ENGLISH);
            locale = Locale.ENGLISH.getLanguage();
        }

        return locale;
    }


}
