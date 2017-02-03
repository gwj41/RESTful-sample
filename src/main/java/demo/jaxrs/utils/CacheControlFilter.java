package demo.jaxrs.utils;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

public class CacheControlFilter implements ContainerResponseFilter {
    private int maxAge;

    public CacheControlFilter(int maxAge) {
        this.maxAge = maxAge;
    }

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getMethod().equals(HttpMethod.GET)) {
            CacheControl cacheControl = new CacheControl();
            cacheControl.setMaxAge(maxAge);
            responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL,cacheControl);
        }
    }
}
