package uz.ehealth.ritme.outbound.metrics;

/**
 * Created by bdcuyp0 on 15-3-2017.
 */
public interface ElapsedTime {
    void start(Class clazz, String method, String... params);

    void stop(String... params);

    void stop();
}
