package demo.jaxrs.utils.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MyExceptionMapper implements ExceptionMapper<Throwable> {
    public Response toResponse(Throwable exception) {
        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.SERVICE_UNAVAILABLE);
        responseBuilder.entity("An exception took place, type is " + exception.getClass().getName());
        return responseBuilder.build();
    }
}
