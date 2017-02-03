package demo.jaxrs.utils.provider;

import demo.jaxrs.utils.CacheControlFilter;
import demo.jaxrs.utils.annotation.MaxAge;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class MaxAgeFeature implements DynamicFeature {
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        MaxAge maxAge = resourceInfo.getResourceMethod().getAnnotation(MaxAge.class);
        if (maxAge == null)
            return;
        CacheControlFilter cacheControlFilter = new CacheControlFilter(maxAge.value());
        context.register(cacheControlFilter);
    }
}
