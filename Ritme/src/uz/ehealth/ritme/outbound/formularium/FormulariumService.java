package uz.ehealth.ritme.outbound.formularium;

/**
 * Created by bdcuyp0 on 25-9-2015.
 */
public interface FormulariumService {

    FormulariumCode getFormulariumCodeForCnk(Integer cnk, String user, final String orgNIHII);
}
