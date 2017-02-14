package be.business.connector.projects.common.utils;

import be.apb.gfddpp.common.constants.SupportedDataTypes;
import be.apb.gfddpp.common.utils.SingleMessageWrapper;
import be.apb.gfddpp.domain.PersonIdType;
import be.apb.gfddpp.helper.SingleMessageValidationHelper;
import be.apb.gfddpp.validation.exception.SingleMessageValidationException;
import be.apb.standards.gfddpp.constants.request.DateRangeTypes;
import be.apb.standards.smoa.schema.id.v1.EntityIdType;
import be.apb.standards.smoa.schema.model.v1.DataLocationType;
import be.apb.standards.smoa.schema.model.v1.MaxSetPersonType;
import be.apb.standards.smoa.schema.model.v1.MaxSetProductType;
import be.apb.standards.smoa.schema.model.v1.MedicationHistoryType;
import be.apb.standards.smoa.schema.v1.PharmaceuticalCareEventType;
import be.apb.standards.smoa.schema.v1.SingleMessage;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.Exceptionutils;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.PropertyHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ValidationUtils {
	static{
		smvh = new SingleMessageValidationHelper();
		//System.out.println("ClassConstruction");
	}

	private static final Logger LOG = Logger.getLogger(ValidationUtils.class);
	private static final SingleMessageValidationHelper smvh;

	public static void validateIncomingFieldsRevokeData(byte[] singleMessage) throws IntegrationModuleException {
		if (singleMessage == null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dataContent"));
		}

	}

	private static void validateSingleMessage(byte[] singleMessage) throws IntegrationModuleException {
		try {
			synchronized (smvh) {
				smvh.assertValidSingleMessage(singleMessage);
			}
		} catch (SingleMessageValidationException e) {
			Exceptionutils.errorHandler(e);
		}
	}

	public static void validateIncomingFieldsGetData(String patientIdType, String patientId, String dataType, String dateRange) throws IntegrationModuleException {

		if (StringUtils.isEmpty(patientId) || StringUtils.isEmpty(patientIdType)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientIdentifier"));
		}

		try {
			PersonIdType.valueOf(patientIdType);
		} catch (IllegalArgumentException e) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientType"));
		}

		if (StringUtils.isEmpty(dataType) || !dataType.toLowerCase().equals(SupportedDataTypes.MEDICATION_HISTORY.getName())) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.data.type"));
		}

		if (StringUtils.isEmpty(dateRange)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.empty"));
		}

		if (!StringUtils.equals(dateRange, DateRangeTypes.FULL.name()) && !StringUtils.equals(dateRange, DateRangeTypes.DEFAULT.name())) {

			if (dateRange.length() != 17 || dateRange.split("-").length != 2) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.structure"));
			}

			String[] dates = dateRange.split("-");

			if (dates[0].length() != 8 || dates[1].length() != 8) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.structure"));
			}

			if (!StringUtils.isNumeric(dates[0]) || !StringUtils.isNumeric(dates[1])) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.notnumeric"));
			}

			if (Integer.parseInt(dates[0]) > Integer.parseInt(dates[1])) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.startdate.larger.than.enddate"));
			}

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String now = format.format(cal.getTime());

			if (Integer.parseInt(dates[0]) > Integer.parseInt(now) || Integer.parseInt(dates[1]) > Integer.parseInt(now)) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.date.range.larger.than.now"));
			}
		}

	}

	public static void validateIncomingFieldsGetDataTypes(String patientId, String patientIdType) throws IntegrationModuleException {

		if (StringUtils.isEmpty(patientId)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientIdentifier"));
		}
		if (StringUtils.isEmpty(patientIdType)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientType"));
		}
		try {
			PersonIdType.valueOf(patientIdType);
		} catch (IllegalArgumentException e) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientType.unknown"));
		}
	}

	public static void validateIncomingFieldsRegisterData(byte[] singleMessage) throws IntegrationModuleException {

		if (singleMessage == null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dataContent"));
		}

	}

	public static void validateIncomingFieldsUpdateData(byte[] singleMessage) throws IntegrationModuleException {
		if (singleMessage == null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dataContent"));
		}

	}

	public static void validateIncomingFieldsGetPharmacyDetails(String patientIdType, String patientId, String dGuid, String motivationText, String motivationType) throws IntegrationModuleException {
		if (StringUtils.isEmpty(patientId)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientIdentifier"));
		}
		if (StringUtils.isEmpty(patientIdType)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientIdType"));
		}

		try {
			PersonIdType.valueOf(patientIdType);
		} catch (IllegalArgumentException e) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patientType"));
		}

		if (StringUtils.isEmpty(dGuid)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dGuid"));
		}
		if (StringUtils.isEmpty(motivationText)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.motivation"));
		}
		if (StringUtils.isEmpty(motivationType)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.motivationType"));
		}
	}

	public static void validateIncomingFieldsGetStatusMessage(String sGuid, String dGuid) throws IntegrationModuleException {
		if (!StringUtils.isEmpty(dGuid) && StringUtils.isEmpty(sGuid)) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.emptysguidbutnotemptydguid"));
		}
	}

	public static void validateExistingTipId(String tipId, PropertyHandler propertyHandler) throws IntegrationModuleException {
		if (tipId == null || SystemServicesUtils.getInstance(propertyHandler).getEndpointOutOfSystemConfiguration(tipId, "TIP", "TIPService") == null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.invalid.tip.id"));
		}
		LOG.info("TIP ID :" + tipId + " validated");
	}

	public static void validateSingleMessageHeader(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);

		if (wrapper.getHeader() != null && wrapper.getHeader().getMessageCreateDate() != null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.header.message.create.date.filled.in"));
		}
		if (wrapper.getHeader() != null && !StringUtils.isEmpty(wrapper.getHeader().getMessageID())) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.header.message.id.filled.in"));
		}
		if (wrapper.getHeader() != null && wrapper.getHeader().getSender() != null) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.header.sender.filled.in"));
		}
		if (wrapper.getHeader() != null && !StringUtils.isEmpty(wrapper.getHeader().getVersion())) {
			throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.header.version.filled.in"));
		}
	}

	public static void validateSingleMessageSGuid(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);
		List<PharmaceuticalCareEventType> pharmaceuticalCareEventTypes = wrapper.getAllEventsOfType(PharmaceuticalCareEventType.class);

		for (Iterator iterator = pharmaceuticalCareEventTypes.iterator(); iterator.hasNext();) {
			PharmaceuticalCareEventType pharmaceuticalCareEventType = (PharmaceuticalCareEventType) iterator.next();
			if (!StringUtils.isEmpty(pharmaceuticalCareEventType.getId())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.sguid.filled.in"));
			}
		}
	}

	public static void validateSingleMessageDGuid(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);

		for (Iterator iterator = wrapper.getAllDispensedProducts().iterator(); iterator.hasNext();) {
			MaxSetProductType product = (MaxSetProductType) iterator.next();
			if (!StringUtils.isEmpty(product.getDispensationGUID())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dguid.filled.in"));
			}
		}
	}

	public static void validateSingleMessageNoSGuid(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);
		List<PharmaceuticalCareEventType> pharmaceuticalCareEventTypes = wrapper.getAllEventsOfType(PharmaceuticalCareEventType.class);

		for (Iterator iterator = pharmaceuticalCareEventTypes.iterator(); iterator.hasNext();) {
			PharmaceuticalCareEventType pharmaceuticalCareEventType = (PharmaceuticalCareEventType) iterator.next();
			if (StringUtils.isEmpty(pharmaceuticalCareEventType.getId())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.sguid.not.filled.in"));

			}

		}

		for (Iterator iterator = wrapper.getAllMedicationHistoryEntries().iterator(); iterator.hasNext();) {
			MedicationHistoryType medicationHistoryType = (MedicationHistoryType) iterator.next();
			if (StringUtils.isEmpty(medicationHistoryType.getSessionID())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.sguid.not.filled.in"));
			}
		}

	}

	public static void validateSingleMessageNoDGuid(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);

		for (Iterator iterator = wrapper.getAllDispensedProducts().iterator(); iterator.hasNext();) {
			MaxSetProductType product = (MaxSetProductType) iterator.next();
			if (StringUtils.isEmpty(product.getDispensationGUID())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dguid.not.filled.in"));
			}
		}

		for (Iterator iterator = wrapper.getAllMedicationHistoryEntries().iterator(); iterator.hasNext();) {
			MedicationHistoryType medicationHistoryType = (MedicationHistoryType) iterator.next();
			if (medicationHistoryType.getEntityId() != null) {
				String dGuid = ((EntityIdType) medicationHistoryType.getEntityId()).getId();
				if (StringUtils.isEmpty(dGuid)) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dguid.not.filled.in"));
				}
			} else {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.dguid.not.filled.in"));
			}
		}

	}

	public static void validateSingleMessageSessionDate(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);
		List<PharmaceuticalCareEventType> pharmaceuticalCareEventTypes = wrapper.getAllEventsOfType(PharmaceuticalCareEventType.class);

		for (Iterator iterator = pharmaceuticalCareEventTypes.iterator(); iterator.hasNext();) {
			PharmaceuticalCareEventType pharmaceuticalCareEventType = (PharmaceuticalCareEventType) iterator.next();
			if (pharmaceuticalCareEventType.getSessionDateTime() != null) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.session.date.filled.in"));

			}

		}
	}

	public static void validateSingleMessageNoSessionDate(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);

		for (Iterator iterator = wrapper.getAllMedicationHistoryEntries().iterator(); iterator.hasNext();) {
			MedicationHistoryType medicationHistoryType = (MedicationHistoryType) iterator.next();
			if (medicationHistoryType.getDeliveryDate() == null) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.delivery.date.not.filled.in"));
			}
		}

	}

	public static void validateSingleMessagePatientMaxDataSet(SingleMessage singleMessageLocal) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageLocal);
		String patientId = null;
		for (Iterator iterator = wrapper.getAllEventsOfType(PharmaceuticalCareEventType.class).iterator(); iterator.hasNext();) {
			PharmaceuticalCareEventType pharmaceuticalCareEventType = (PharmaceuticalCareEventType) iterator.next();
			if (pharmaceuticalCareEventType.getMaxPatient() != null) {
				MaxSetPersonType maxSetPersonType = pharmaceuticalCareEventType.getMaxPatient().getIdentification();
				PersonIdType personIdType = null;
				if (maxSetPersonType != null) {
					personIdType = PersonIdType.valueOf(maxSetPersonType.getPersonId());
				} else {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.id.not.filled.in"));
				}
				String pId = personIdType.getIdFrom(maxSetPersonType.getPersonId());

				if (StringUtils.isEmpty(pId)) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.id.not.filled.in"));
				} else if (patientId == null) {
					patientId = pId;
				} else if (!pId.equals(patientId)) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.id.not.equal.to.parameters"));
				}
			} else {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.not.filled.in"));
			}
		}
	}

	public static void validateSingleMessagePatient(String patientid, SingleMessage singleMessageLocal) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageLocal);

		for (Iterator iterator = wrapper.getAllMedicationHistoryEntries().iterator(); iterator.hasNext();) {
			MedicationHistoryType medicationHistoryType = (MedicationHistoryType) iterator.next();
			if (medicationHistoryType.getMinPatient() != null) {
				PersonIdType personIdType = PersonIdType.valueOf(medicationHistoryType.getMinPatient().getPersonId());
				String pId = personIdType.getIdFrom(medicationHistoryType.getMinPatient().getPersonId());
				if (StringUtils.isEmpty(pId)) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.id.not.filled.in"));
				} else if (!pId.equals(patientid)) {
					throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.id.not.equal.to.parameters"));
				}
			} else {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.patient.not.filled.in"));
			}
		}

	}

	/**
	 * Fires an error whenever the motivation's "free text" is empty.
	 * 
	 * @param singleMessageObject
	 *            The object to be checked
	 * @throws IntegrationModuleException
	 *             The exception thrown when the text field is empty
	 */
	public static void validateMotivationIsProvided(SingleMessage singleMessageObject) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessageObject);

		for (Iterator iterator = wrapper.getAllDispensedProducts().iterator(); iterator.hasNext();) {
			MaxSetProductType product = (MaxSetProductType) iterator.next();
			if (product.getMotivation() == null) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.motivation.not.filled.in"));
			}
			if (StringUtils.isEmpty(product.getMotivation().getFreeText())) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.motivation.not.filled.in"));
			}
		}
	}

	public static void validateDataTypesResult(SingleMessage smo) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(smo);

		for (Iterator iterator = wrapper.getAllEntitiesOfType(DataLocationType.class).iterator(); iterator.hasNext();) {
			DataLocationType dlt = (DataLocationType) iterator.next();
			if (dlt.getLocation() == null) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.empty.datatype.response"));
			}
			if (dlt.getLocation().isEmpty()) {
				throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.empty.datatype.response"));
			}
		}
	}

	public static void validateSessionDateTime(SingleMessage singleMessage) throws IntegrationModuleException {
		SingleMessageWrapper wrapper = new SingleMessageWrapper(singleMessage);
		XMLGregorianCalendar sessionDateTime = null;
		for (PharmaceuticalCareEventType pharmaceuticalCareEventType : wrapper.getAllEventsOfType(PharmaceuticalCareEventType.class)) {
			if (pharmaceuticalCareEventType.getSessionDateTime() != null) {
				sessionDateTime = pharmaceuticalCareEventType.getSessionDateTime();
				Date sessionDateTimeDate = sessionDateTime.toGregorianCalendar().getTime();
				Calendar validationDate = Calendar.getInstance(); 
			    validationDate.setTime(new Date()); 
			    validationDate.add(Calendar.HOUR_OF_DAY, 2); 
				if(sessionDateTimeDate.after(validationDate.getTime())){
					throw new IntegrationModuleException(I18nHelper.getLabel("pharmaceuticalCareEventType.session.date.time.in.future"));
				}
			} else {
				throw new IntegrationModuleException(I18nHelper.getLabel("pharmaceuticalCareEventType.session.date.time.not.found"));
			}

		}
	}

	public static boolean isValidCbfa(List<String> cbfas, String cbfa) {
		if (cbfas.contains(cbfa)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void validatePatientId(String patientId)
			throws IntegrationModuleException {
		if (patientId.length() == 11) {
			Integer checkDigit = Integer.parseInt(patientId.substring(9, 11));
			String r = patientId.substring(0, 9);

			Integer rest = Integer.parseInt(r) % 97;

			if (!String.valueOf(checkDigit).equals(String.valueOf(97 - rest))) {
				r = "2" + r;
				long restL = Long.parseLong(r) % 97;
				if (!String.valueOf(checkDigit).equals(
						String.valueOf(97 - restL))) {
					throw new IntegrationModuleException(
							I18nHelper
									.getLabel("error.validation.patientid.incorrect"));
				}
			}
		} else {
			throw new IntegrationModuleException(
					I18nHelper.getLabel("error.validation.patientid.incorrect"));
		}
	}
}
