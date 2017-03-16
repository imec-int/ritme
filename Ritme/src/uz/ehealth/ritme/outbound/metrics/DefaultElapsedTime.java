package uz.ehealth.ritme.outbound.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bdcuyp0 on 15-3-2017.
 */
public class DefaultElapsedTime implements ElapsedTime {
    public static final Logger LOG = LoggerFactory.getLogger(ElapsedTime.class);
    private long startMillis = 0;
    private String[] params = null;
    private Class clazz;
    private String method;

    @Override
    public void start(final Class clazz, final String method, final String... params) {
        startMillis = System.currentTimeMillis();
        this.params = params;
        this.clazz = clazz;
        this.method = method;
    }

    @Override
    public void stop(final String... params) {
        long stopMillis = System.currentTimeMillis();
        if (startMillis != 0) {
            int iterations = Math.max(this.params != null ? this.params.length : 0, params != null ? params.length : 0);
            String message = (clazz != null ? clazz.getSimpleName() : "") + "::" + method + ";" + (stopMillis - this.startMillis) + ";";
            for (int i = 0; i < iterations; i++) {
                final String key;
                final String value;
                if (this.params != null && i < this.params.length) {
                    key = this.params[i];
                } else {
                    key = "null";
                }
                if (params != null && i < params.length) {
                    value = params[i];
                } else {
                    value = "null";
                }
                message += (key + "=" + value + ";");
            }

            LOG.info(message);
        }

    }

    @Override
    public void stop() {
        stop((String[]) null);
    }
}
