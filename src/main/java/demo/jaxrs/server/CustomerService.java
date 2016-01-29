package demo.jaxrs.server;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by wgu on 9/2/2015.
 */
@Path("/customerservice/")
//@Produces("text/xml")
@Produces("application/xml")
public interface CustomerService {
    @GET
    @Path("/customers/{id}/")
    @Produces("application/xml")
    Customer getCustomer(@PathParam("id") PathSegment id);

    @GET
    @Path("/singleCustomer/")
    @Produces(MediaType.TEXT_HTML)
    Customer getSingleCustomer();

    @GET
    @Path("/customers/streamingOutput/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    StreamingOutput getSingleCustomer1(@PathParam("id") String id);

    @GET
    @Path("/customers/{id: .+}/Age/")
    @Produces("application/json")
    Response getCustomerWithMultiPathSegments(@PathParam("id") List<PathSegment> id);

    @GET
    @Path("/customers/{id}/{firstName}/UriInfo/")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomerWithUriInfo(@Context UriInfo uriInfo);

    @GET
    @Path("/file/{filePath: .*}/")
    @Produces({MediaType.TEXT_PLAIN})
    File getCustomerAsFile(@PathParam("filePath") String filePath);

    @GET
    @Path("/customers/{id}/")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Customer getCustomer1(@PathParam("id") String id);

    @GET
    @Path("/customers/json2/{id}/")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    Response getCustomer2(@PathParam("id") String id);

    @GET
    @Path("/customers/json2/{firstName}-{lastName}/")
    @Produces("application/json")
    Response getCustomer3(@PathParam("firstName") String firstName,@PathParam("lastName") String lastName);

    @GET
    @Path("/queryParam")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomer4(@Encoded @QueryParam("first") List<String> firstName,@DefaultValue("Gu") @QueryParam("last") String lastName);

    @GET
    @Path("/allCustomers/list/")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    List<Customer> getCustomer5();

    @GET
    @Path("/allCustomers/map/")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Map<Long, Customer> getCustomer6();

    @GET
    @Path("/allCustomers/map/response/")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomer7();

    @PUT
    @Path("/customers/")
    Response updateCustomer(Customer customer);

    @POST
    @Path("/customers/")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces("application/json")
    Response addCustomer(Customer customer);

    @POST
    @Path("/customers/form/")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response addCustomer1(@FormParam("firstName") String firstName,@FormParam("lastName") String lastName, @HeaderParam("Referer") URL referer,
                          @Context HttpHeaders headers);

    @POST
    @Path("/customers/multiValuedMap/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_FORM_URLENCODED})
    MultivaluedMap<String,String> addCustomerWithMultiValuedMap(MultivaluedMap<String,String> form);

    @POST
    @Path("/customers/form/bean")
    @Produces("application/json")
    Response addCustomer2(@BeanParam Customer customer);

    @DELETE
    @Path("/customers/{id}/")
    Response deleteCustomer(@PathParam("id") String id);

    @GET
    @Path("/orders/{orderId}/")
    Order getOrder(@PathParam("orderId") String orderId);
}
