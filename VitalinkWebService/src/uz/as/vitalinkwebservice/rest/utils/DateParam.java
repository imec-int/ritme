package uz.as.vitalinkwebservice.rest.utils;

import java.util.Date;

/**
 * Created by bdcuyp0 on 20-5-2016.
 */
public class DateParam {
    public Date getDate() {
        return date;
    }

    private final Date date;

    public DateParam(String iso8601Date) {
        this.date = javax.xml.bind.DatatypeConverter.parseDateTime(iso8601Date).getTime();
    }
}
