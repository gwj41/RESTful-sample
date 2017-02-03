package demo.jaxrs.utils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Provider
@Priority(value = Priorities.ENTITY_CODER)
public class GZIPDecoder implements ReaderInterceptor {
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        String encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
        if (!"gzip".equalsIgnoreCase(encoding))
            return context.proceed();
        GZIPInputStream gzipInputStream = new GZIPInputStream(context.getInputStream());
        context.setInputStream(gzipInputStream);
        return context.proceed();
    }
}
