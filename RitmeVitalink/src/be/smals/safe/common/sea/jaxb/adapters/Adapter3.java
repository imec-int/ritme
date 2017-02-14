//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package be.smals.safe.common.sea.jaxb.adapters;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Adapter3 extends XmlAdapter<String, Calendar> {
    public Adapter3() {
    }

    @Override
    public Calendar unmarshal(String value) {
        return DatatypeConverter.parseTime(value);
    }

    @Override
    public String marshal(Calendar value) {
        return value == null ? null : new SimpleDateFormat("HH:mm:ss").format(value.getTime());
    }
}
