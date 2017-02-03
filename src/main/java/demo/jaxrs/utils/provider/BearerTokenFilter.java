package demo.jaxrs.utils.provider;

import demo.jaxrs.utils.annotation.TokenAuthenticated;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
@TokenAuthenticated
public class BearerTokenFilter implements ContainerRequestFilter {
    public void filter(ContainerRequestContext ctx) throws IOException {
        String authHeader = ctx.getHeaderString(HttpHeaders.WWW_AUTHENTICATE);
        if (authHeader == null) throw new NotAuthorizedException("Bearer");
        String token = parseToken(authHeader);
        if (verifyToken(token) == false) {
            throw new NotAuthorizedException("Bearer error=\"invalid_token\"");
        }
    }
    private String parseToken(String header) {
        return header.length() + header;
    }
    private boolean verifyToken(String token) {
        int length = token.length();
        return length == 6;
    }
}
