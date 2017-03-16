package uz.ehealth.ritme.outbound.metrics;

/**
 * Created by bdcuyp0 on 15-3-2017.
 */
public interface Metrics {
    ElapsedTime getElapsedTime();

    ElapsedTime startElapsedTime(Class clazz, String method, String... params);
}
