package demo.jaxrs.utils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@Provider
@Priority(value = Priorities.ENTITY_CODER)
public class GZIPEncoder implements WriterInterceptor{

    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(context.getOutputStream());
        context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING,"gzip");
        context.setOutputStream(gzipOutputStream);
        context.proceed();
    }
}
