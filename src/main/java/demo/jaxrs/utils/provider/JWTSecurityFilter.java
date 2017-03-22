package demo.jaxrs.utils.provider;

import demo.jaxrs.server.Customer;
import demo.jaxrs.server.CustomerFactory;
import demo.jaxrs.utils.KeyUtil;
import demo.jaxrs.utils.TokenUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.util.Iterator;
import java.util.Map;

@Provider
public class JWTSecurityFilter implements ContainerRequestFilter{
    private Log logger = LogFactory.getLog(JWTSecurityFilter.class);
    @Context
    private HttpServletRequest request;
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String requestMethod = requestContext.getMethod();
        if (requestMethod.equals(HttpMethod.GET)) {
            String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            Key key = KeyUtil.getKey();
            if (TokenUtil.isValid(token, key)){
                String name = TokenUtil.getName(token, key);//反解出Name
                String[] roles = TokenUtil.getRoles(token, key);//反解出角色
                int version = TokenUtil.getVersion(token, key);//得到版本
                if (name != null && roles.length != 0 && version != -1) {
                    Customer customer = null;
                    Map<Long,Customer> customers = CustomerFactory.instance.getCustomers();
                    for (Iterator<Map.Entry<Long, Customer>> it = customers.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Long, Customer> entry = it.next();
                        if (entry.getValue().getFirstName().equalsIgnoreCase(name)) {
                            customer = entry.getValue();
                        }
                    }
                    if (customer != null) {
                        requestContext.setProperty("customer",customer);
                        return;
                    } else {
                        logger.info("User not found " + name);
                    }
                } else {
                    logger.info("name, roles or version missing from token");
                }
            }
            else {
                logger.info("token is invalid");

            }
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
