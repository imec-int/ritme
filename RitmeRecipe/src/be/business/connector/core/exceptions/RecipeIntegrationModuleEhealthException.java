package be.business.connector.core.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Created by bdcuyp0 on 11-7-2016.
 */
public class RecipeIntegrationModuleEhealthException extends IntegrationModuleEhealthException {
    public RecipeIntegrationModuleEhealthException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RecipeIntegrationModuleEhealthException(final String message) {
        super(message);
    }

    public RecipeIntegrationModuleEhealthException(final Throwable cause) {
        super(cause);
    }

    @Nullable
    private String getCustomMessage(final String locale, final Throwable cause) {
        if (cause instanceof be.recipe.client.services.prescriber.RecipeException) {
            be.recipe.client.services.prescriber.RecipeExceptionDetails e = ((be.recipe.client.services.prescriber.RecipeException) cause).getFaultInfo();
            be.recipe.client.services.prescriber.RecipeExceptionDetails.ErrorMap list = e.getErrorMap();
            for (be.recipe.client.services.prescriber.RecipeExceptionDetails.ErrorMap.Entry entry : list.getEntries()) {
                if (entry.getKey().startsWith(locale)) {
                    return entry.getValue().getMessage() + "\n" + entry.getValue().getResolution();
                }
            }
        }

        if (cause instanceof be.recipe.client.services.executor.RecipeException) {
            be.recipe.client.services.executor.RecipeExceptionDetails e = ((be.recipe.client.services.executor.RecipeException) cause).getFaultInfo();
            be.recipe.client.services.executor.RecipeExceptionDetails.ErrorMap list = e.getErrorMap();
            for (be.recipe.client.services.executor.RecipeExceptionDetails.ErrorMap.Entry entry : list.getEntries()) {
                if (entry.getKey().startsWith(locale)) {
                    return entry.getValue().getMessage() + "\n" + entry.getValue().getResolution();
                }
            }
        }
        return null;
    }
}
