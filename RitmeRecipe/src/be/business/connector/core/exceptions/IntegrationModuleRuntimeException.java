/**
 * Copyright (C) 2010 Recip-e
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.business.connector.core.exceptions;

import java.util.Locale;

import org.apache.log4j.Logger;

public class IntegrationModuleRuntimeException extends RuntimeException {

	private final static Logger LOG = Logger.getLogger(IntegrationModuleRuntimeException.class);

	private static final long serialVersionUID = 1L;
	
	
	public IntegrationModuleRuntimeException(String message, Throwable cause) {
		super(message, cause);
		LOG.error(message,this);
	}


	public IntegrationModuleRuntimeException(String message) {
		super(message);
		LOG.error(message,this);
	}

	public IntegrationModuleRuntimeException(Throwable cause) {
		super(cause);
		LOG.error("IntegrationModuleException",cause);
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
		
		if(cause instanceof be.recipe.client.services.prescriber.RecipeException){
			be.recipe.client.services.prescriber.RecipeExceptionDetails e =((be.recipe.client.services.prescriber.RecipeException)cause).getFaultInfo();
			be.recipe.client.services.prescriber.RecipeExceptionDetails.ErrorMap list = e.getErrorMap();
			for(be.recipe.client.services.prescriber.RecipeExceptionDetails.ErrorMap.Entry entry : list.getEntries()){
				if( entry.getKey().startsWith(locale) ){
					return entry.getValue().getMessage() + "\n" + entry.getValue().getResolution();
				}
			}
		}
		
		if(cause instanceof be.recipe.client.services.executor.RecipeException){
			be.recipe.client.services.executor.RecipeExceptionDetails e =((be.recipe.client.services.executor.RecipeException)cause).getFaultInfo();
			be.recipe.client.services.executor.RecipeExceptionDetails.ErrorMap list = e.getErrorMap();
			for(be.recipe.client.services.executor.RecipeExceptionDetails.ErrorMap.Entry entry : list.getEntries()){
				if( entry.getKey().startsWith(locale) ){
					return entry.getValue().getMessage() + "\n" + entry.getValue().getResolution();
				}
			}
		}
	
		
		// Local error
		return super.getMessage();
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
