package be.business.connector.core.utils;

import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealer;

import java.security.Key;

/**
 * Created by bdcuyp0 on 21-3-2016.
 */
public class EncryptionStuff {
    private final EncryptionUtils encryptionUtils;
    private final PropertyHandler propertyHandler;
    private final Key symmetricKey;
    private final DataSealer oldDataSealer;
    private final DataUnsealer oldDataUnsealer;
    private final ETKHelper etkHelper;

    public EncryptionStuff(final EncryptionUtils encrUtils, final PropertyHandler propHandler, final Key symmKey, final DataSealer oldDataSealer, final DataUnsealer oldDataUnsealer, final ETKHelper etkHelper) {
        this.encryptionUtils = encrUtils;
        this.propertyHandler = propHandler;
        this.symmetricKey = symmKey;
        this.oldDataSealer = oldDataSealer;
        this.oldDataUnsealer = oldDataUnsealer;
        this.etkHelper = etkHelper;
    }
}
