package demo.jaxrs.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class CustomerDelegate {
//    @GET
    @Path("delegate")
    public Customer getCustomer(@PathParam("id") long id) {
        return CustomerFactory.instance.getCustomers().get(id);
    }
}
