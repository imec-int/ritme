package uz.ehealth.ritme.outbound.hospital;

/**
 * Created by bdcuyp0 on 11-1-2016.
 */
public interface HospitalService {
    HospitalData getData(final String nihiiOrg, final String user);
}
