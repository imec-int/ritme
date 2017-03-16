package uz.ehealth.ritme.outbound.metrics;

/**
 * Created by bdcuyp0 on 15-3-2017.
 */
public class DefaultMetrics implements Metrics {
    @Override
    public ElapsedTime getElapsedTime() {
        return new DefaultElapsedTime();
    }

    @Override
    public ElapsedTime startElapsedTime(final Class clazz, final String method, final String... params) {
        DefaultElapsedTime time = new DefaultElapsedTime();
        time.start(clazz, method, params);
        return time;
    }
}
