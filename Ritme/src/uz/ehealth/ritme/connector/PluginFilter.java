package uz.ehealth.ritme.connector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by bdcuyp0 on 25-8-2016.
 */
public class PluginFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginFilter.class);
    private Filter pluggedFilter;


    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        String classNameProperty = filterConfig.getInitParameter("filterClassNameProperty");
        if (!StringUtils.isEmpty(classNameProperty)) {
            try {
                pluggedFilter = PluginManager.get(classNameProperty, Filter.class);
                pluggedFilter.init(filterConfig);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (pluggedFilter != null) {
            pluggedFilter.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        if (pluggedFilter != null) {
            pluggedFilter.destroy();
        }

    }
}
