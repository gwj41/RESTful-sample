package demo.jaxrs.server;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by wgu on 9/17/2015.
 */
@Provider
public class EntityNotFoundMapper implements ExceptionMapper {
    public Response toResponse(Throwable exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
