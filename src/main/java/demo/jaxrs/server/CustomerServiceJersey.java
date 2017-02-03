package demo.jaxrs.server;

import demo.jaxrs.utils.annotation.MaxAge;
import demo.jaxrs.utils.annotation.Pretty;
import demo.jaxrs.utils.annotation.TokenAuthenticated;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("customerservice")
//@Produces("text/xml")
@Produces("application/xml")
public interface CustomerServiceJersey {
//    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    CustomerDelegate getCustomer();

    @POST
    @Path("customer/stream")
    @Produces(MediaType.APPLICATION_JSON)
    Customer addCustomerByStream(InputStream inputStream) throws IOException;

    @GET
    @Path("customers/streamingOutput/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    StreamingOutput getSingleCustomer1(@PathParam("id") String id);

    @GET
    @Path("customers/{id: .+}/Age")
    @Produces("application/json")
    Response getCustomerWithMultiPathSegments(@PathParam("id") List<PathSegment> id,@Context Request request);

    @GET
    @Path("customers/{id}/{firstName}/UriInfo")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomerWithUriInfo(@Context UriInfo uriInfo);

    @GET
    @Path("file/{filePath: .*}")
    @Produces({MediaType.TEXT_PLAIN})
    File getCustomerAsFile(@PathParam("filePath") String filePath);

    @GET
    @Path("customers/{id}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @TokenAuthenticated
    @MaxAge(1234)
    Customer getCustomer1(@PathParam("id") PathSegment id) throws SQLException;

    @GET
    @Path("customers/json2/{id}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    Response getCustomer2(@PathParam("id") String id);

    @GET
    @Path("customers/json2/{firstName}-{lastName}")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @Pretty
    Response getCustomer3(@PathParam("firstName") String firstName,@PathParam("lastName") String lastName);

    @GET
    @Path("queryParam")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomer4(@Encoded @QueryParam("first") List<String> firstName,@DefaultValue("Gu") @QueryParam("last") String lastName);

    @GET
    @Path("allCustomers/list")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    List<Customer> getCustomer5();

    @GET
    @Path("allCustomers/map")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Map<Long, Customer> getCustomer6();

    @GET
    @Path("allCustomers/map/response")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response getCustomer7();

    @PUT
    @Path("customers/{id}")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces("application/json")
    Response updateCustomer(@PathParam("id") long id,Customer customer);

    @POST
    @Path("customers")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces("application/json")
    Response addCustomer(Customer customer);

    @POST
    @Path("customers/form")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    Response addCustomer1(@FormParam("firstName") String firstName,@FormParam("lastName") String lastName, @HeaderParam("Referer") URL referer,
                          @Context HttpHeaders headers);

    @POST
    @Path("customers/multiValuedMap")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_FORM_URLENCODED})
    Response addCustomerWithMultiValuedMap(MultivaluedMap<String,String> form);

    @POST
    @Path("customers/bean")
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    Response addCustomer2(@BeanParam Customer customer);

    @DELETE
    @Path("customers/{id}")
    Response deleteCustomer(@PathParam("id") String id);

    @GET
    @Path("orders/{orderId}")
    Order getOrder(@PathParam("orderId") String orderId);

    @GET
    @Path("cars/{make}/{model:.*}/year/{year}")
    @Produces(MediaType.TEXT_PLAIN)
    File getPictureWithPathSegmentList(@PathParam("make") String make,@PathParam("model") List<PathSegment> model,@PathParam("year") int year);

    @GET
    @Path("cars/{make}/{model}/year/{year}/")
    @Produces({MediaType.APPLICATION_JSON})
    Car getPictureWithPathSegment(@PathParam("make") String make,@PathParam("model") PathSegment model,@PathParam("year") int year,@Context UriInfo uriInfo);

    @GET
    @Path("cars/{model:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    Car getPictureWithPathSegmentList(@PathParam("model") List<PathSegment> model);

    @GET
    @Path("customers/generic")
    @Produces(MediaType.APPLICATION_JSON)
    Response getCustomerList();
}
