package demo.jaxrs.server;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import demo.jaxrs.utils.JSONTool;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerServiceJerseyImpl implements CustomerServiceJersey {
    long currentId = 123;
    private Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private Map<Long, Order> orders = new ConcurrentHashMap<Long, Order>();
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private HttpHeaders httpHeaders;
    @HeaderParam(HttpHeaders.USER_AGENT)
    private String userAgent;
    private CustomerDelegate customerDelegate;

    public CustomerServiceJerseyImpl() {
        init();
        customerDelegate = new CustomerDelegate();
    }

    public CustomerDelegate getCustomer() {
        return customerDelegate;
    }
    /**
     * No2
     */
    public Customer addCustomerByStream(InputStream inputStream) throws IOException {
        byte[] input = readFromStream(inputStream);
        String inputStrng = new String(input);
        return JSONTool.toObject(inputStrng, Customer.class);
    }

    private byte[] readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1000];
        int wasRead;
        while ((wasRead = inputStream.read(buffer)) > -1) {
            baos.write(buffer, 0, wasRead);
        }
        return baos.toByteArray();
    }

    /**
     * No3
     */
    public StreamingOutput getSingleCustomer1(String id) {
        System.out.println("----return StreamingOutput getCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        final Customer c = customers.get(idNumber);
        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                String outputStr;
                if (c != null) {
                    outputStr = "Name is: " + c.getFirstName() + " " + c.getLastName();
                    output.write(outputStr.getBytes());
                } else {
                    output.write("Customer does not exist!".getBytes());
                }

            }
        };
    }

    /**
     * No4
     */
    public Response getCustomerWithMultiPathSegments(List<PathSegment> id, Request request) {
        System.out.println("----invoking getCustomer With Multiple PathSegments, size is: " + id.size());
        for (PathSegment pathSegment : id) {
            System.out.println("----Path segment getPath: " + pathSegment.getPath());
            System.out.println("----Path segment toString: " + pathSegment.toString());
            MultivaluedMap<String, String> matrixParameters = pathSegment.getMatrixParameters();
            Set<String> matrixKey = matrixParameters.keySet();
            for (String key : matrixKey) {
                System.out.println("Key is " + key + ",value is " + matrixParameters.get(key));
            }
        }
        Customer customer = customers.get(Long.valueOf(id.get(0).getPath()));
        EntityTag tag = new EntityTag(String.valueOf(customer.hashCode()));
        CacheControl cache = new CacheControl();
        cache.setMaxAge(1000);
        cache.setSMaxAge(2000);
        Locale locale = Locale.CHINA;
        Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(new Date(), tag);
        if (responseBuilder != null) {
            responseBuilder.cacheControl(cache);
            return responseBuilder.build();
        }
        return Response.ok(customer).cacheControl(cache).language(locale).tag(tag).lastModified(new Date()).build();
    }

    /**
     * No5
     */
    public Response getCustomerWithUriInfo(UriInfo uriInfo) {
        System.out.println("----invoking getCustomer With UriInfo , path is: " + uriInfo.getPath() + ",then redirecting ...");
        URI baseURI = uriInfo.getBaseUri();
        URI uri = UriBuilder.fromUri(baseURI).path("/customerservice/customers/json2/{id}/").build("123");
        MultivaluedMap<String, String> pathParameters = uriInfo.getPathParameters();
        for (String key : pathParameters.keySet()) {
            System.out.println("----Path segment: " + pathParameters.get(key));
        }
        System.out.println("----Redirect to : " + uri.toString());
        CacheControl cache = new CacheControl();
        cache.setNoCache(true);
        Response response = Response.temporaryRedirect(uri).build();
//        return response;
        return Response.ok().build();
    }

    /**
     * No6
     */
    public File getCustomerAsFile(String filePath) {
        return new File(this.getClass().getResource("/" + filePath).getFile());
    }

    /**
     * No7
     */
    public Customer getCustomer1(PathSegment id) throws SQLException {
        System.out.println("----invoking getCustomer, Customer gender is: " + id.getMatrixParameters().getFirst("gender"));
        System.out.println("----PathSegment invoking getCustomer, Customer id is: " + id.getPath());
        UriBuilder uriBuilder = UriBuilder.fromMethod(CustomerServiceJersey.class, "getCustomer1");
        Link link = Link.fromUriBuilder(uriBuilder).rel("edit").type(MediaType.TEXT_PLAIN).build(id);
        long idNumber = Long.parseLong(id.getPath());
        Customer c = customers.get(idNumber);
        if (c == null) {
            throw new SQLException();
        }
        c.getLinks().clear();
        c.getLinks().add(link);
        Customer c1;
        JAXBContext ctx;
        try {
            ctx = JAXBContext.newInstance(Customer.class);
            StringWriter writer = new StringWriter();
            ctx.createMarshaller().marshal(c, writer);
            String custString = writer.toString();
            c1 = (Customer) ctx.createUnmarshaller()
                    .unmarshal(new StringReader(custString));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * No8
     */
    public Response getCustomer2(String id) {
        System.out.println("----invoking getCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            m.acceptJsonFormatVisitor(m.constructType(Customer.class), visitor);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }
        JsonSchema jsonSchema = visitor.finalSchema();
//        jsonSchema.asStringSchema().toString();
//        System.out.println(jsonSchema.);
        if (c != null) {
            return Response.ok(c).build();
        } else {
            String errormsg = "<error>Customer Not Found!</error>";
            Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
//            builder.type(MediaType.APPLICATION_JSON_TYPE);
//            builder.type(MediaType.APPLICATION_XML);
            builder.type(MediaType.TEXT_HTML);
            builder.entity(errormsg);
//            builder.entity("<error>Customer Not Found!</error>");
//            return Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(builder.build());
/*            NotFoundException exception = new NotFoundException();
            Response.ResponseBuilder builder2 = Response.status(exception.getResponse().getStatus());
            builder2.type(MediaType.APPLICATION_XML);
            builder2.entity(errormsg);
            throw new NotFoundException(builder2.build());*/
        }
    }

    /**
     * No9
     */
    public Response getCustomer3(String firstName, String lastName) {
        System.out.println("----invoking getCustomer, Customer name is: " + firstName + " " + lastName);
        Annotation annotation = CustomerServiceJersey.class
                .getAnnotation(Path.class);
        Set<Long> customerKey = customers.keySet();
        for (Long key : customerKey) {
            Customer c = customers.get(key);
            if (c.getFirstName().equals(firstName) && c.getLastName().equals(lastName)) {
                return Response.ok().entity(c, new Annotation[]{annotation}).cookie(new NewCookie("ABC", "CNN")).header("txid", 998997).type(MediaType.APPLICATION_XML).build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * No10
     */
    public Response getCustomer4(List<String> firstName, String lastName) {
        System.out.println("----invoking QueryParam getCustomer, Customer name is: " + firstName + " " + lastName);
        /*for (String nameStr : name) {
            System.out.println("Name is " + nameStr);
        }*/
        return Response.ok(firstName).build();
    }

    /**
     * No11
     */
    public List<Customer> getCustomer5() {
//        GenericEntity entity = new GenericEntity<Map<Long, Customer>>(customers,Map.class){};
        return new ArrayList(customers.values());
    }

    /**
     * No12
     */
    public Map<Long, Customer> getCustomer6() {
        return customers;
    }

    /**
     * No13
     */
    public Response getCustomer7() {
        return Response.ok(customers).build();
    }

    /**
     * No14
     */
    public Response updateCustomer(long id, Customer customer) {
        System.out.println("----invoking updateCustomer, Customer name is: " + customer.getFirstName() + " " + customer.getLastName());
        Customer c = customers.get(id);
        Link link = Link.fromMethod(CustomerServiceJersey.class, "getCustomer1").rel("edit").type(MediaType.TEXT_PLAIN).build(id);
        Response r;
        if (c != null) {
            c.setLastName(customer.getLastName());
            c.getLinks().add(link);
            System.out.println("User Agent is " + userAgent);
//            customers.put(customer.getId(), customer);
            r = Response.ok(c).location(UriBuilder.fromUri(uriInfo.getBaseUriBuilder().build()).build()).links(link).build();
        } else {
            r = Response.notModified().build();
        }
        return r;
    }

    /**
     * No15
     */
    public Response addCustomer(Customer customer) {
        System.out.println("----invoking addCustomer, Customer name is: " + customer.getFirstName() + " " + customer.getLastName());
        customer.setId(++currentId);

        customers.put(customer.getId(), customer);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
//        URI customerId = UriBuilder.path("/customers/").path("{id}").build(customer.getId());
        return Response.created(customerId).entity(customer).status(Response.Status.FORBIDDEN).build();
/*        return Response.created(URI.create("/customers/"
                + customer.getId())).entity(customer).build();*/
//        return Response.ok(customer).build();
    }

    /**
     * No16
     */
    public Response addCustomer1(String firstName, String lastName, URL referer, HttpHeaders headers) {
        System.out.println("Start @FormParam,@HeaderParam test...");
        for (Map.Entry headerEntry : headers.getRequestHeaders().entrySet()) {
            System.out.println("Header " + headerEntry.getKey() + ":" + headerEntry.getValue());
        }
        for (Map.Entry<String, Cookie> cookieEntry : headers.getCookies().entrySet()) {
            Cookie cookie = cookieEntry.getValue();
            System.out.println("Name: " + cookie.getName() + ",Value:" + cookie.getValue() + ",Path:" + cookie.getPath());
        }
        Customer customer = new Customer();
        customer.setId(++currentId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customers.put(customer.getId(), customer);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
//        return Response.status(Response.Status.CREATED).build();
        // set Content-Encoding
//        return Response.created(customerId).header(HttpHeaders.CONTENT_ENCODING,"gzip").entity(customer).build();
        return Response.created(customerId).entity(customer).build();
    }

    /**
     * No17
     */
    public Response addCustomerWithMultiValuedMap(MultivaluedMap<String, String> form) {
        return Response.ok(form).build();
    }

    /**
     * No18
     */
    public Response addCustomer2(Customer customer) {
        System.out.println("Start @BeanParam test...");
        customer.setId(++currentId);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
        return Response.created(customerId).entity(customer).build();
    }

    /**
     * No19
     */
    @Path("/customers/{id}/")
    public Response deleteCustomer(@PathParam("id") String id) {
        System.out.println("----invoking deleteCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);

        Response r;
        if (c != null) {
            r = Response.ok().build();
            customers.remove(idNumber);
        } else {
            r = Response.notModified().build();
        }

        return r;
    }

    /**
     * No20
     */
    @Path("/orders/{orderId}/")
    public Order getOrder(@PathParam("orderId") String orderId) {
        System.out.println("----invoking getOrder, Order id is: " + orderId);
        long idNumber = Long.parseLong(orderId);
        Order c = orders.get(idNumber);
        return c;
    }

    /**
     * No21
     */
    public File getPictureWithPathSegmentList(String make, List<PathSegment> model, int year) {
        System.out.println("----invoking getPictureWithPathSegmentList, Make is: " + make);
        for (Iterator<PathSegment> carIterator = model.iterator(); carIterator.hasNext(); ) {
            PathSegment car = carIterator.next();
            System.out.println("----Model is: " + car.getPath());
            MultivaluedMap<String, String> map = car.getMatrixParameters();
            System.out.println("----Color is: " + map.get("color"));
            System.out.println("----Weight is: " + map.get("weight"));
        }
        String filePath = Thread.currentThread().getContextClassLoader().getResource("images/a.jpg").getFile();
        File file = new File(filePath);
        return file;
    }

    /**
     * No22
     */
    public Car getPictureWithPathSegment(String make, PathSegment model, int year, UriInfo uriInfo) {
        System.out.println("----invoking getPictureWithPathSegmentList, Make is: " + make);
        System.out.println("----Model is: " + model.getPath());
        MultivaluedMap<String, String> map = model.getMatrixParameters();
        System.out.println("----Color is: " + map.getFirst("color"));
        System.out.println("----Weight is: " + map.getFirst("weight"));
        System.out.println("----Year is: " + year);
        Car car = new Car(1, make, model.getPath(), map.getFirst("color"), map.getFirst("weight"), year);
        String make1 = uriInfo.getPathParameters().getFirst("make");
        PathSegment model1 = uriInfo.getPathSegments().get(3);
        URI relativizeUri;
        URI baseURI;
        URI resolveURI;
        try {
            relativizeUri = uriInfo.relativize(new URI("a/b/c"));
            resolveURI = uriInfo.resolve(new URI("a/b/c"));
            baseURI = uriInfo.getBaseUri();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultivaluedMap<String, String> matrixParameters = model1.getMatrixParameters();
        String color = model1.getMatrixParameters().getFirst("color");
        return car;
    }

    /**
     * No23
     */
    public Car getPictureWithPathSegmentList(List<PathSegment> model) {
        for (Iterator<PathSegment> carIterator = model.iterator(); carIterator.hasNext(); ) {
            PathSegment car = carIterator.next();
            System.out.println("----Model is: " + car.getPath());
        }
        Car car = new Car();
        return car;
    }

    /**
     * No24
     */
    public Response getCustomerList() {
        List<Customer> list = new ArrayList<>();
        list.add(customers.get(123));
        list.add(customers.get(111));
        GenericEntity<List<Customer>> entity = new GenericEntity(list, List.class);
//        GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(list){};
        return Response.ok(entity).build();
    }

    final void init() {
        customers = CustomerFactory.instance.getCustomers();

        Order o = new Order();
        o.setDescription("order 223");
        o.setId(223);
        orders.put(o.getId(), o);
    }


}
