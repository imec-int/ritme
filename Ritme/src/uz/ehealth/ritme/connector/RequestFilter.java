package uz.ehealth.ritme.connector;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by bdcuyp0 on 6-7-2016.
 */
public class RequestFilter implements Filter {

    private static final ThreadLocal<ServletRequest> CURRENT_REQUEST = new ThreadLocal<ServletRequest>();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        CURRENT_REQUEST.set(request);
        chain.doFilter(request, response);
        CURRENT_REQUEST.remove();
    }

    @Override
    public void destroy() {

    }

    public static ServletRequest getCurrentRequest() {
        return CURRENT_REQUEST.get();
    }
}
