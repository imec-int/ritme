package be.business.connector.recipe.prescriber;

import be.business.connector.common.CommonIntegrationModule;
import be.business.connector.common.RequestorProvider;
import be.business.connector.core.domain.IdentifierTypes;
import be.business.connector.core.domain.KgssIdentifierType;
import be.business.connector.core.ehealth.services.KgssService;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.module.AbstractIntegrationModule;
import be.business.connector.core.technical.connector.utils.Crypto;
import be.business.connector.core.utils.*;
import be.business.connector.projects.common.utils.ValidationUtils;
import be.business.connector.recipe.prescriber.services.RecipePrescriberServiceImpl;
import be.business.connector.recipe.utils.KmehrHelper;
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionToken;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.*;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.CreatePrescriptionResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.GetPrescriptionForPrescriberResponse;
import be.fgov.ehealth.recipe.protocol.v1.prescriber.ListFeedbacksResponse;
import be.recipe.client.services.prescriber.*;
import com.sun.xml.ws.client.ClientTransportException;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.perf4j.aop.Profiled;
import uz.ehealth.ritme.outbound.metrics.DefaultMetrics;
import uz.ehealth.ritme.outbound.metrics.ElapsedTime;
import uz.ehealth.ritme.outbound.metrics.Metrics;
import uz.ehealth.ritme.plugins.PluginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrescriberIntegrationModuleImpl extends AbstractIntegrationModule implements PrescriberIntegrationModule {
    private static final Logger LOG = Logger.getLogger(PrescriberIntegrationModuleImpl.class);

    private Map<String, KeyResult> keyCache = new HashMap<String, KeyResult>();
    private CommonIntegrationModule commonIntegrationModule;
    private RequestorProvider requestorIntegrationModule;
    private KgssService kgssService = be.business.connector.core.ehealth.services.KgssServiceImpl.getInstance();
    private KmehrHelper kmehrHelper;

    public PrescriberIntegrationModuleImpl() throws IntegrationModuleException {
        super();
        if (PropertyHandler.getInstance() != null) {
            kmehrHelper = new KmehrHelper(PropertyHandler.getInstance().getProperties());
        } else {
            //this is for the mock!
            kmehrHelper = new KmehrHelper(new PropertyHandler().getProperties());
        }
    }

    /**
     * Instantiates a new prescriber services client.
     *
     * @param propertyFile the property file
     * @throws IntegrationModuleException the integration module exception
     */
    public PrescriberIntegrationModuleImpl(CommonIntegrationModule commonIntegrationModule, RequestorProvider requestorIntegrationModule) throws IntegrationModuleException {
        super();
        kmehrHelper = new KmehrHelper(commonIntegrationModule.getPropertyHandler().getProperties());
        this.commonIntegrationModule = commonIntegrationModule;
        this.requestorIntegrationModule = requestorIntegrationModule;
        initEncryption(commonIntegrationModule.getPropertyHandler());
    }

    /**
     * Prepare create prescription.
     *
     * @param patientId the patient id
     * @param nihiiOrg
     * @throws IntegrationModuleException
     */
    @Override
    @Profiled(logFailuresSeparately = true, tag = "PrescriberIntegrationModule#prepareCreatePrescription")
    public void prepareCreatePrescription(String patientId, String prescriptionType, final String nihiiOrg) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);

        String cacheId = "(" + patientId + "#" + prescriptionType + "#" + nihiiOrg + ")";
        keyCache.put(cacheId, getNewKeyFromKgss(prescriptionType, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)), null, patientId, getEtkHelper(nihiiOrg).getSystemETK().get(0).getEncoded(), nihiiOrg));
    }

    /**
     * Gets the new key.
     *
     * @param patientId        the patient id
     * @param prescriptionType the prescription type
     * @param nihiiOrg
     * @return the new key
     * @throws IntegrationModuleException the integration module exception
     */
    private KeyResult getNewKey(String patientId, String prescriptionType, final String nihiiOrg) throws IntegrationModuleException {
        KeyResult key = null;

        String cacheId = "(" + patientId + "#" + prescriptionType + "#" + nihiiOrg + ")";
        if (keyCache.containsKey(cacheId)) {
            key = keyCache.get(cacheId);
            keyCache.remove(cacheId);
        } else {
            key = getNewKeyFromKgss(prescriptionType, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)), null, patientId, getEtkHelper(nihiiOrg).getSystemETK().get(0).getEncoded(), nihiiOrg);
        }
        return key;
    }

    @Override
    @Profiled(logFailuresSeparately = true, tag = "PrescriberIntegrationModule#ping")
    public void ping(final String nihiiOrg) throws IntegrationModuleException {

        AliveCheckResponse response = null;
        try {
            response = RecipePrescriberServiceImpl.getInstance().aliveCheck(new AliveCheckRequest(), nihiiOrg);
        } catch (ClientTransportException cte) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
        }
        LOG.info("Ping response : " + response.getAliveCheckResult());
        checkStatus(response);

    }

    /**
     * Creates the prescription.
     *
     * @param feedbackRequested the feedback requested
     * @param patientId         the patient id
     * @param prescription      the prescription
     * @param prescriptionType  the prescription type
     * @return the string
     * @throws IntegrationModuleException the integration module exception
     */
    @Override
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#createPrescription")
    public String createPrescription(boolean feedbackRequested, long patientId, byte[] prescription, String prescriptionType, String nihiiOrg) throws IntegrationModuleException {
        final Metrics metrics = PluginManager.get("uz.ritme.outbound.metrics", Metrics.class, DefaultMetrics.class);
        commonIntegrationModule.assertValidSession(nihiiOrg);

        if (patientId == 0) {
            throw new IntegrationModuleException("Patient ID is 0.");
        }

        String pid = ETKHelper.longToString(patientId, 11);
        ValidationUtils.validatePatientId(pid);
        try {
            ElapsedTime time0 = metrics.startElapsedTime(this.getClass(), "prescriptionValidation");
            getKmehrHelper().assertValidKmehrPrescription(prescription, prescriptionType);
            time0.stop();

            // init helper
            ElapsedTime time1 = metrics.startElapsedTime(this.getClass(), "createMarshaller");
            MarshallerHelper<CreatePrescriptionResult, CreatePrescriptionParam> helper = new MarshallerHelper<CreatePrescriptionResult, CreatePrescriptionParam>(CreatePrescriptionResult.class, CreatePrescriptionParam.class);
            time1.stop();

            // get recipe etk
            ElapsedTime time2 = metrics.startElapsedTime(this.getClass(), "getRecipe_ETK");
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();
            time2.stop();

            // create sealed prescription
            ElapsedTime startCompress = metrics.startElapsedTime(this.getClass(), "compress");
            byte[] message = IOUtils.compress(prescription);
            startCompress.stop();

            ElapsedTime newKeyTime = metrics.startElapsedTime(this.getClass(), "getNewKey");
            KeyResult key = getNewKey(pid, prescriptionType, nihiiOrg);
            newKeyTime.stop();
            ElapsedTime startSealUnknown = metrics.startElapsedTime(this.getClass(), "sealUnknown");
            message = sealPrescriptionForUnknown(key, message, nihiiOrg);
            startSealUnknown.stop();

            // create sealed content
            CreatePrescriptionParam params = new CreatePrescriptionParam();
            params.setPatientId(pid);
            params.setFeedbackRequested(feedbackRequested);
            params.setPrescription(message);
            params.setPrescriptionType(prescriptionType);
            ElapsedTime startEncodeSymmKey = metrics.startElapsedTime(this.getClass(), "encodeSymmKey");
            params.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
            startEncodeSymmKey.stop();
            params.setKeyId(key.getKeyId());
            params.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            // create request
            CreatePrescriptionRequest request = new CreatePrescriptionRequest();
            ElapsedTime startSeal = metrics.startElapsedTime(this.getClass(), "seal");
            request.setSecuredCreatePrescriptionRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(params), nihiiOrg)));
            startSeal.stop();

            // create administrative info
            CreatePrescriptionAdministrativeInformationType info = new CreatePrescriptionAdministrativeInformationType();
            request.setAdministrativeInformation(info);
            ElapsedTime startBase64Decode = metrics.startElapsedTime(this.getClass(), "base64Decode");
            info.setKeyIdentifier(Base64.decode(key.getKeyId()));
            startBase64Decode.stop();
            // info.setKeyIdentifier(key.getKeyId().getBytes());
            info.setPrescriptionType(prescriptionType);
            info.setPatientIdentifier(createIdentifierType(pid, IdentifierTypes.SSIN.name()));
            info.setPrescriberIdentifier(createIdentifierType(ETKHelper.longToString(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)), 11), IdentifierTypes.NIHII11.name()));

            // WS call
            CreatePrescriptionResponse response = RecipePrescriberServiceImpl.getInstance().createPrescription(request, nihiiOrg);
            checkStatus(response);

            // unseal response

            ElapsedTime startUnseal = metrics.startElapsedTime(this.getClass(), "unseal");
            CreatePrescriptionResult result = helper.unsealWithSymmKey(response.getSecuredCreatePrescriptionResponse().getSecuredContent(), getSymmKey(nihiiOrg));
            startUnseal.stop();

            return result.getRid();
        } catch (Throwable t) {

            Exceptionutils.errorHandler(t);
        }

        return null;
    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#createPrescriptionByServer")
    private CreatePrescriptionResponse createPrescriptionServer(CreatePrescriptionRequest request, final String nihiiOrg) throws IntegrationModuleException {
        try {
            CreatePrescriptionResponse response = RecipePrescriberServiceImpl.getInstance().createPrescription(request, nihiiOrg);
            return response;
        } catch (ClientTransportException cte) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
        }

    }

    /**
     * Cancel prescription.
     *
     * @param rid         the rid
     * @param reason      the reason
     * @param nihiiOrg
     * @param patientSSIN
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#revokePrescription")
    @Override
    public void revokePrescription(String rid, String reason, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // init helper
            MarshallerHelper<Object, RevokePrescriptionParam> helper = new MarshallerHelper<Object, RevokePrescriptionParam>(Object.class, RevokePrescriptionParam.class);

            // get Recipe ETK
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // create params
            RevokePrescriptionParam params = new RevokePrescriptionParam();
            params.setRid(rid);
            params.setReason(reason);
            params.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            // create request
            RevokePrescriptionRequest request = new RevokePrescriptionRequest();
            request.setSecuredRevokePrescriptionRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(params), nihiiOrg)));

            // Admin Info for eHealth
            PrescriberServiceAdministrativeInformationType info = new PrescriberServiceAdministrativeInformationType();
            info.setPatientIdentifier(createIdentifierType(patientSSIN, IdentifierTypes.SSIN.name()));

            request.setAdministrativeInformation(info);

            // call WS
            try {
                checkStatus(RecipePrescriberServiceImpl.getInstance().revokePrescription(request, nihiiOrg));
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }

        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }

    }

    /**
     * Gets the prescription.
     *
     * @param rid         the rid
     * @param nihiiOrg
     * @param patientSSIN
     * @return the prescription
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#getPrescription")
    @Override
    public GetPrescriptionForPrescriberResult getPrescription(String rid, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // init helper
            MarshallerHelper<GetPrescriptionForPrescriberResult, GetPrescriptionForPrescriberParam> helper = new MarshallerHelper<GetPrescriptionForPrescriberResult, GetPrescriptionForPrescriberParam>(GetPrescriptionForPrescriberResult.class, GetPrescriptionForPrescriberParam.class);

            // get recipe etk
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // create sealed request
            GetPrescriptionForPrescriberParam param = new GetPrescriptionForPrescriberParam();
            param.setRid(rid);
            param.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
            param.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            // build request
            GetPrescriptionForPrescriberRequest request = new GetPrescriptionForPrescriberRequest();
            request.setSecuredGetPrescriptionForPrescriberRequest(createSecuredContentType((sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg))));

            PrescriberServiceAdministrativeInformationType info = new PrescriberServiceAdministrativeInformationType();

            // id added
            info.setPatientIdentifier(createIdentifierType(patientSSIN, IdentifierTypes.SSIN.name()));
            request.setAdministrativeInformation(info);

            // call sealed WS

            GetPrescriptionForPrescriberResponse response = null;

            try {
                response = RecipePrescriberServiceImpl.getInstance().getPrescriptionForPrescriber(request, nihiiOrg);
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }

            checkStatus(response);

            // unseal WS response
            GetPrescriptionForPrescriberResult result = helper.unsealWithSymmKey(response.getSecuredGetPrescriptionForPrescriberResponse().getSecuredContent(), getSymmKey(nihiiOrg));

            KeyResult key = getKeyFromKgss(result.getEncryptionKeyId(), getEtkHelper(nihiiOrg).getSystemETK().get(0).getEncoded(), nihiiOrg);
            byte[] unsealedPrescription = IOUtils.decompress(unsealPrescriptionForUnknown(key, result.getPrescription(), nihiiOrg));
            result.setPrescription(unsealedPrescription);


            return result;
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    /**
     * List open prescription.
     *
     * @param patientId
     * @param nihiiOrg
     * @param prescriberId
     * @return the list for a geiven patient.
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#listOpenPrescription")
    @Override
    public List<String> listOpenPrescription(String patientId, final String nihiiOrg, final Long prescriberId) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {
            // init helper
            MarshallerHelper<GetListOpenPrescriptionResult, GetListOpenPrescriptionParam> helper = new MarshallerHelper<GetListOpenPrescriptionResult, GetListOpenPrescriptionParam>(GetListOpenPrescriptionResult.class, GetListOpenPrescriptionParam.class);

            // get recipe etk
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // create param
            GetListOpenPrescriptionParam param = new GetListOpenPrescriptionParam();
            param.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
            param.setPrescriberId(prescriberId);
            param.setPatientId(patientId);

            // create request
            ListOpenPrescriptionsRequest request = new ListOpenPrescriptionsRequest();
            request.setSecuredListOpenPrescriptionsRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));

            // call sealed WS
            ListOpenPrescriptionsResponse response = null;
            try {
                response = RecipePrescriberServiceImpl.getInstance().listOpenPrescriptions(request, nihiiOrg);
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }

            checkStatus(response);

            // unseal WS response
            GetListOpenPrescriptionResult result = helper.unsealWithSymmKey(response.getSecuredListOpenPrescriptionsResponse().getSecuredContent(), getSymmKey(nihiiOrg));

            return result.getPrescriptions();

        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }

        return null;
    }

    /**
     * List open prescription.
     *
     * @param nihiiOrg
     * @return the list
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#listOpenPrescription")
    @Override
    public List<String> listOpenPrescription(final String nihiiOrg) throws IntegrationModuleException {
        return listOpenPrescription(null, nihiiOrg, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));
    }

    /**
     * Address prescription.
     *
     * @param notificationText the notification text
     * @param patientId        the patient id
     * @param executorId       the executor id
     * @param nihiiOrg
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "PrescriberIntegrationModule#sendNotification")
    @Override
    public void sendNotification(byte[] notificationText, String patientId, long executorId, final String nihiiOrg) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {
            getKmehrHelper().assertValidNotification(notificationText);
            ValidationUtils.validatePatientId(patientId);

            // init helper
            MarshallerHelper<Object, SendNotificationParam> helper = new MarshallerHelper<Object, SendNotificationParam>(Object.class, SendNotificationParam.class);

            // get recipe etk
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // get recipient etk
            List<EncryptionToken> etkRecipients = getEtkHelper(nihiiOrg).getEtks(KgssIdentifierType.NIHII_PHARMACY, executorId);

            byte[] notificationZip = IOUtils.compress(notificationText);

            for (int i = 0; i < etkRecipients.size(); i++) {
                EncryptionToken etkRecipient = etkRecipients.get(0);

                byte[] notificationSealed = sealNotification(etkRecipient, notificationZip, nihiiOrg);

                // create param
                SendNotificationParam param = new SendNotificationParam();
                param.setContent(notificationSealed);
                param.setExecutorId(executorId);
                param.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));
                param.setPatientId(patientId);

                // create request
                SendNotificationRequest request = new SendNotificationRequest();
                request.setSecuredSendNotificationRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
                SendNotificationAdministrativeInformationType info = new SendNotificationAdministrativeInformationType();
                info.setExecutorIdentifier(createIdentifierType(ETKHelper.longToString(executorId, 11), IdentifierTypes.SSIN.name()));
                info.setPatientIdentifier(createIdentifierType(patientId, IdentifierTypes.SSIN.name()));
                request.setAdministrativeInformation(info);

                // call sealed WS
                try {
                    checkStatus(RecipePrescriberServiceImpl.getInstance().sendNotification(request, nihiiOrg));
                } catch (ClientTransportException cte) {
                    throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
                }
            }
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
    }

    /**
     * Update feedback flag.
     *
     * @param rid             the rid
     * @param feedbackAllowed the feedback allowed
     * @param nihiiOrg
     * @param patientSSIN
     * @throws IntegrationModuleException the integration module exception
     */
    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#updateFeedbackFlag")
    @Override
    public void updateFeedbackFlag(String rid, boolean feedbackAllowed, final String nihiiOrg, final String patientSSIN) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // init helper
            MarshallerHelper<Object, UpdateFeedbackFlagParam> helper = new MarshallerHelper<Object, UpdateFeedbackFlagParam>(Object.class, UpdateFeedbackFlagParam.class);

            // get recipe etk
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // create param
            UpdateFeedbackFlagParam param = new UpdateFeedbackFlagParam();
            param.setAllowFeedback(feedbackAllowed);
            param.setRid(rid);
            param.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            UpdateFeedbackFlagRequest request = new UpdateFeedbackFlagRequest();
            request.setSecuredUpdateFeedbackFlagRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));
            PrescriberServiceAdministrativeInformationType info = new PrescriberServiceAdministrativeInformationType();

            // Admin Info for eHealth
            info.setPatientIdentifier(createIdentifierType(patientSSIN, IdentifierTypes.SSIN.name()));
            request.setAdministrativeInformation(info);

            // call sealed WS
            try {
                checkStatus(RecipePrescriberServiceImpl.getInstance().updateFeedbackFlag(request, nihiiOrg));
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }

    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#listFeedback")
    @Override
    public List<ListFeedbackItem> listFeedback(boolean readFlag, final String nihiiOrg) throws IntegrationModuleException {
        commonIntegrationModule.assertValidSession(nihiiOrg);
        try {

            // check if personal password has been set
            List<EncryptionToken> personalETKs = getEtkHelper(nihiiOrg).getEtks(KgssIdentifierType.NIHII, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            getEncryptionUtils(nihiiOrg).verifyDecryption(personalETKs.get(0));

            // init helper
            MarshallerHelper<ListFeedbacksResult, ListFeedbacksParam> helper = new MarshallerHelper<ListFeedbacksResult, ListFeedbacksParam>(ListFeedbacksResult.class, ListFeedbacksParam.class);

            // get recipe etk
            final List<EncryptionToken> etkRecipes = getEtkHelper(nihiiOrg).getRecipe_ETK();

            // create param
            ListFeedbacksParam param = new ListFeedbacksParam();
            param.setReadFlag(readFlag);
            param.setSymmKey(getSymmKey(nihiiOrg).getEncoded());
            param.setPrescriberId(Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));

            // create request
            ListFeedbacksRequest request = new ListFeedbacksRequest();
            request.setSecuredListFeedbacksRequest(createSecuredContentType(sealRequest(etkRecipes.get(0), helper.toXMLByteArray(param), nihiiOrg)));

            // call sealed WS
            ListFeedbacksResponse response = null;

            try {
                response = RecipePrescriberServiceImpl.getInstance().listFeedbacks(request, nihiiOrg);
            } catch (ClientTransportException cte) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.connection.prescriber"), cte);
            }
            checkStatus(response);

            // unseal WS response
            List<ListFeedbackItem> feedbacks = helper.unsealWithSymmKey(response.getSecuredListFeedbacksResponse().getSecuredContent(), getSymmKey(nihiiOrg)).getFeedbacks();

            for (int i = 0; i < feedbacks.size(); i++) {
                be.business.connector.recipe.prescriber.domain.ListFeedbackItem item = new be.business.connector.recipe.prescriber.domain.ListFeedbackItem(feedbacks.get(i));
                byte[] content = item.getContent();
                try {
                    content = unsealFeedback(content, nihiiOrg);
                    content = IOUtils.decompress(content);
                    item.setContent(content);
                } catch (Throwable t) {
                    item.setLinkedException(t);
                }
                feedbacks.set(i, item);
            }
            return feedbacks;

        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    private SecuredContentType createSecuredContentType(byte[] content) {
        SecuredContentType secured = new SecuredContentType();
        secured.setSecuredContent(content);
        return secured;
    }

    private void checkStatus(ResponseType response) throws IntegrationModuleException {
        if (!EHEALTH_SUCCESS_CODE_100.equals(response.getStatus().getCode()) && !EHEALTH_SUCCESS_CODE_200.equals(response.getStatus().getCode())) {
            LOG.error("Error Status received : " + response.getStatus().getCode());
            throw new IntegrationModuleException(getLocalisedMsg(response.getStatus()));
        }
    }

    private String getLocalisedMsg(StatusType status) {
        final String locale = IntegrationModuleException.getUserLocale();
        for (LocalisedString msg : status.getMessages()) {
            if (msg.getLang() != null && locale.equalsIgnoreCase(msg.getLang().value())) {
                return msg.getValue();
            }
        }
        if (status.getMessages().size() > 0) {
            return status.getMessages().get(0).getValue();
        }
        return status.getCode();
    }

    private IdentifierType createIdentifierType(final String id, final String type) {
        IdentifierType ident = new IdentifierType();
        ident.setId(id + "");
        ident.setType(type);
        return ident;
    }


    @Override
    public void setPersonalPassword(String personalPassword, final String nihiiOrg) throws IntegrationModuleException {
        List<EncryptionToken> tokens = null;

        if (commonIntegrationModule.getNiss() == null) {
            throw new IntegrationModuleException("CreateSession() must invoked first");
        }

        try {

            commonIntegrationModule.getEncryptionUtils(nihiiOrg).unlockPersonalKey(commonIntegrationModule.getNiss(), personalPassword);
            dataUnsealer = commonIntegrationModule.getEncryptionUtils(nihiiOrg).initUnsealing();
            tokens = getEtkHelper(nihiiOrg).getEtks(KgssIdentifierType.NIHII, Long.valueOf(requestorIntegrationModule.getRequestorIdInformation(nihiiOrg)));
        } catch (Exception e) {
            throw new IntegrationModuleException(e);
        }

        commonIntegrationModule.getEncryptionUtils(nihiiOrg).verifyDecryption(tokens.get(0));

    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#unsealFeedback")
    protected byte[] unsealFeedback(byte[] message, final String nihiiOrg) throws IntegrationModuleException {
        return unsealNotiffeed(message, nihiiOrg);
    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#getNewKeyFromKgss")
    protected KeyResult getNewKeyFromKgss(String prescriptionType, Long prescriberId, Long executorId, String patientId, byte[] myEtk, final String nihiiOrg) throws IntegrationModuleException {
        // For test, when a sim key is specified in the config
        if (getPropertyHandler().hasProperty("test_kgss_key")) {
            return getKeyFromKgss(null, null, nihiiOrg);
        }

        EncryptionToken etkKgss = getEtkHelper(nihiiOrg).getKGSS_ETK().get(0);
        List<String> credentialTypes = commonIntegrationModule.getPropertyHandler().getMatchingProperties("kgss.createPrescription.ACL." + prescriptionType);

        KeyResult keyResult = null;
        try {
            keyResult = kgssService.retrieveNewKey(etkKgss.getEncoded(), credentialTypes, prescriberId, executorId, patientId, myEtk, nihiiOrg);
        } catch (Throwable t) {
            Exceptionutils.errorHandler(t);
        }
        return keyResult;
    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#sealNotification")
    protected byte[] sealNotification(EncryptionToken paramEncryptionToken, byte[] paramArrayOfByte, final String nihiiOrg) throws IntegrationModuleException {
        return Crypto.seal(paramEncryptionToken, paramArrayOfByte, nihiiOrg);
    }

    @Profiled(logFailuresSeparately = true, tag = "0.PrescriberIntegrationModule#sealPrescriptionForUnknown")
    protected byte[] sealPrescriptionForUnknown(KeyResult key, byte[] messageToProtect, final String nihiiOrg) throws IntegrationModuleException {
        return Crypto.seal(messageToProtect, key.getSecretKey(), key.getKeyId(), nihiiOrg);
    }

    private KmehrHelper getKmehrHelper() {
        return kmehrHelper;
    }
}
