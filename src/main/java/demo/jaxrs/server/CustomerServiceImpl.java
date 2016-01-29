/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package demo.jaxrs.server;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;


public class CustomerServiceImpl implements CustomerService {
    long currentId = 123;
    Map<Long, Customer> customers = new HashMap<Long, Customer>();
    Map<Long, Order> orders = new HashMap<Long, Order>();

    public CustomerServiceImpl() {
        init();
    }


    public Customer getCustomer(PathSegment id) {
        System.out.println("----invoking getCustomer, Customer gender is: " + id.getMatrixParameters().getFirst("gender"));
        System.out.println("----invoking getCustomer, Customer id is: " + id.getPath());
        long idNumber = Long.parseLong(id.getPath());
        Customer c = customers.get(idNumber);
        return c;
    }

    public Customer getSingleCustomer() {
        Customer c = new Customer();
        c.setFirstName("John1");
        c.setLastName("Smith1");
        c.setId(999);
        return c;
    }

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

    public Response getCustomerWithMultiPathSegments(List<PathSegment> id) {
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
        return Response.ok().build();
    }

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

    public File getCustomerAsFile(String filePath) {
        return new File(this.getClass().getResource("/" + filePath).getFile());
    }


    public Customer getCustomer1( String id) {
        System.out.println("----invoking getCustomer, Customer id is: " + id);
        long idNumber = Long.parseLong(id);
        Customer c = customers.get(idNumber);
        Customer c1;
        JAXBContext ctx;
        try {
            ctx = JAXBContext.newInstance(Customer.class);
            StringWriter writer = new StringWriter();
            ctx.createMarshaller().marshal(c, writer);
            String custString = writer.toString();
            c1 = (Customer)ctx.createUnmarshaller()
                    .unmarshal(new StringReader(custString));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return c;
    }

    public Response getCustomer2( String id) {
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
            Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
//            builder.type(MediaType.APPLICATION_JSON_TYPE);
            builder.type(MediaType.APPLICATION_XML);
            builder.entity("<error>Customer Not Found!</error>");
//            return Response.status(Response.Status.BAD_REQUEST).build();
            throw new WebApplicationException(builder.build());
        }
    }

    public Response getCustomer3(String firstName, String lastName) {
        System.out.println("----invoking getCustomer, Customer name is: " + firstName + " " + lastName);
        Set<Long> customerKey = customers.keySet();
        for (Long key : customerKey) {
            Customer c = customers.get(key);
            if (c.getFirstName().equals(firstName) && c.getLastName().equals(lastName)) {
                return Response.ok(c).build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    public Response getCustomer4(List<String> firstName,String lastName) {
        System.out.println("----invoking QueryParam getCustomer, Customer name is: " + firstName + " " + lastName);
        /*for (String nameStr : name) {
            System.out.println("Name is " + nameStr);
        }*/
        return Response.status(Response.Status.OK).build();
    }

    public List<Customer> getCustomer5() {
//        GenericEntity entity = new GenericEntity<Map<Long, Customer>>(customers,Map.class){};
        return new ArrayList(customers.values());
    }

    public Map<Long, Customer> getCustomer6() {
        return customers;
    }

    public Response getCustomer7() {
        return Response.ok(customers).build();
    }

    public Response updateCustomer(Customer customer) {
        System.out.println("----invoking updateCustomer, Customer name is: " + customer.getFirstName() + " " + customer.getLastName());
        Customer c = customers.get(customer.getId());
        Response r;
        if (c != null) {
            customers.put(customer.getId(), customer);
            r = Response.ok().build();
        } else {
            r = Response.notModified().build();
        }

        return r;
    }

    public Response addCustomer(Customer customer) {
        System.out.println("----invoking addCustomer, Customer name is: " + customer.getFirstName() + " " + customer.getLastName());
        customer.setId(++currentId);

        customers.put(customer.getId(), customer);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
//        URI customerId = UriBuilder.path("/customers/").path("{id}").build(customer.getId());
        return Response.created(customerId).entity(customer).build();
/*        return Response.created(URI.create("/customers/"
                + customer.getId())).entity(customer).build();*/
//        return Response.ok(customer).build();
    }

    public Response addCustomer1(String firstName, String lastName,URL referer,HttpHeaders headers) {
        System.out.println("Start @FormParam,@HeaderParam test...");
        Customer customer = new Customer();
        customer.setId(++currentId);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customers.put(customer.getId(), customer);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
//        return Response.status(Response.Status.CREATED).build();
        return Response.created(customerId).entity(customer).build();
    }

    public MultivaluedMap<String, String> addCustomerWithMultiValuedMap(MultivaluedMap<String, String> form) {
        return form;
    }

    public Response addCustomer2(Customer customer) {
        System.out.println("Start @BeanParam test...");
        customer.setId(++currentId);
        URI customerId = UriBuilder.fromResource(Customer.class).path("{id}").build(customer.getId());
        return Response.created(customerId).entity(customer).build();
    }

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

    public Order getOrder(@PathParam("orderId") String orderId) {
        System.out.println("----invoking getOrder, Order id is: " + orderId);
        long idNumber = Long.parseLong(orderId);
        Order c = orders.get(idNumber);
        return c;
    }

    final void init() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Smith");
        c.setId(123);
        c.setWeight("90");
        Address address = new Address();
        address.setCity("Suzhou");
        address.setState("Jiangsu");
        address.setStreet("Xinghu");
        address.setZip("215123");
        c.setAddress(address);
        customers.put(c.getId(), c);

        Customer c1 = new Customer();
        c1.setFirstName("Wenjun");
        c1.setLastName("Gu");
        c1.setId(111);
        c1.setWeight("65");
        Address address1 = new Address();
        address1.setCity("Shanghai");
        address1.setState("Pudong");
        address1.setStreet("Nanjing Road");
        address1.setZip("123987");
        c1.setAddress(address1);
        customers.put(c1.getId(), c1);

        Order o = new Order();
        o.setDescription("order 223");
        o.setId(223);
        orders.put(o.getId(), o);
    }

}
