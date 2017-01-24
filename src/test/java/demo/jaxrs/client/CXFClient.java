package demo.jaxrs.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import demo.jaxrs.server.Address;
import demo.jaxrs.server.Customer;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@RunWith(BlockJUnit4ClassRunner.class)
public class CXFClient {
    private static final String BASE_LOCATION = "http://localhost:8080/RESTful-sample/service/info/";
    private WebClient client1;
    private javax.ws.rs.client.Client client2;
    private javax.ws.rs.client.Client client3;
    private Response response;
    @Before
    public void init() {
        client1 = WebClient.create(BASE_LOCATION);
        client2 = ClientBuilder.newClient();
        client3 = ClientBuilder.newBuilder().property("connection.timeout",100).register(JacksonJaxbJsonProvider.class).build();
    }
    @Test
    public void cxf1() {
        System.out.println("Sent WebClient test...");
        client1 = client1.path("customerservice/customers/{id};gender=male","123").accept(MediaType.APPLICATION_XML);
        System.out.println(client1.get(String.class));
    }

    @Test
    public void cxf2() throws Exception {
        System.out.println("Add customer test...");
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer.txt").getFile();
        File input = new File(inputFile);
        response = client1.path("customerservice/customer/stream").accept(MediaType.APPLICATION_JSON).post(input);
        response.bufferEntity();
        System.out.println("Status: " + response.getStatus());
        System.out.println("Location: " + response.getLocation());
        System.out.println(getStringFromInputStream((InputStream) (response.getEntity())));
        System.out.println("Result of response.readEntity: " + response.readEntity(String.class));
    }

    @Test
    public void cxf3() {
        System.out.println("Sent WebClient test...get StreamingOutput result");
        System.out.println(client1.path("customerservice/customers/streamingOutput/{id}", "123").accept(new String[]{MediaType.TEXT_PLAIN}).get(String.class));
    }

    @Test
    public void cxf4() {
        System.out.println("Invoking getCustomer With Multiple PathSegments");
        System.out.println(client1.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/Age",new String[]{"123","Wenjun"}).accept(MediaType.APPLICATION_JSON).get(String.class));
    }

    @Test
    public void cxf5() {
        System.out.println("Invoking getCustomer With UriInfo");
        System.out.println(client1.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/UriInfo", new String[]{"123", "Wenjun"}).get(String.class));
    }

    @Test
    public void cxf6() {
        System.out.println("Return File object...");
        File file = client1.path("customerservice/file/{filePath}", "customerFile.txt").accept(MediaType.TEXT_PLAIN).get(File.class);
        System.out.println("File is: " + file.getPath());
    }

    @Test
    public void cxf7() {
        System.out.println("Sent WebClient test...get json and xml format");
        System.out.println(client1.path("customerservice/customers/{id}", "123").accept(new String[]{MediaType.APPLICATION_JSON}).get(String.class));
    }

    @Test
    public void cxf8() {
        System.out.println("Sent json2 WebClient test...");
        System.out.println(client1.path("customerservice/customers/json2/{id}", "1234").accept(MediaType.APPLICATION_JSON).get(String.class));
    }

    @Test
    public void cxf9() {
        System.out.println("Sent json2 WebClient test,get Wenjun Gu...");
        String[] name = {"Wenjun","Gu"};
        System.out.println(client1.path("customerservice/customers/json2/{firstName}-{lastName}", name).accept(MediaType.APPLICATION_JSON).get(String.class));
    }

    @Test
    public void cxf10() {
        String[] name = {"Wenjun","Gu"};
        System.out.println("Test @QueryParam List...");
//        System.out.println(client7.path("customerservice/queryParam").query("first", "Gu").replaceQueryParam("first",name).get(String.class));
        System.out.println(client1.path("customerservice/queryParam").replaceQueryParam("first", name).get(Response.class));
    }

    @Test
    public void cxf11() {
        System.out.println("Return complex type List");
        String customers = client1.path("customerservice/allCustomers/list/").accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);
    }

    @Test
    public void cxf12() {
        System.out.println("Return complex type Map");
        String customers = client1.path("customerservice/allCustomers/map/").accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);
    }

    @Test
    public void cxf13() {
        System.out.println("Return complex type Map in Response");
        response = client1.path("customerservice/allCustomers/map/response/").accept(MediaType.APPLICATION_JSON).get();
        response.bufferEntity();
        String resultMap = response.readEntity(String.class);
        System.out.println(resultMap);
    }

    @Test
    public void cxf14() {
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer2.txt").getFile();
        File input = new File(inputFile);
        client1.type("application/json;charset=UTF-8");
        response = client1.path("customerservice/customers/{id}","123").accept(MediaType.APPLICATION_JSON).put(input);
        response.bufferEntity();
        System.out.println("Status: " + response.getStatus());
        System.out.println("Location: " + response.getLocation());
    }

    @Test
    public void cxf15_1() throws Exception {
        System.out.println("Add customer test...");
//        Thread.currentThread().getClass().getClassLoader().getResourceAsStream("/customer.txt");
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer.txt").getFile();
        File input = new File(inputFile);
        client1.type("application/json;charset=UTF-8");
        response = client1.path("customerservice/customers").accept(MediaType.APPLICATION_JSON).post(input);
        response.bufferEntity();
        System.out.println("Status: " + response.getStatus());
        System.out.println("Family: " + response.getStatusInfo().getFamily());
        System.out.println(getStringFromInputStream((InputStream) (response.getEntity())));
        System.out.println("Result of response.readEntity: " + response.readEntity(String.class));
    }

    @Test
    public void cxf15_2() {
        System.out.println("Add customer1 test...");
        String inputFile1 = Thread.currentThread().getContextClassLoader().getResource("customer1.txt").getFile();
        File input1 = new File(inputFile1);
        client1.type("application/xml;charset=UTF-8");
        response = client1.path("customerservice/customers").accept(MediaType.APPLICATION_JSON).post(input1);
        System.out.println("Location is " + response.getLocation());
        System.out.println("Response Status is " + response.getStatus());
    }

    @Test
    public void cxfJAXRSClient1() {
        System.out.println("*** GET Created Customer **");
        String customer = client2.target(BASE_LOCATION + "customerservice/customers/{id}").resolveTemplate("id",123).request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customer);
    }

    /**
     * No16
     */
    @Test
    public void cxfJAXRSClient2() {
        System.out.println("*** GET Created Customer,post Form **");
        Form form = new Form();
        form.param("firstName","Jianbing");
        form.param("lastName","Gao");
        response = client2.target(BASE_LOCATION + "customerservice/customers/form").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void cxf17() {
        System.out.println("Post customer with MultivaluedMap...");
        MultivaluedMap map = new MultivaluedHashMap();
        map.add("firstName","Jianbing");
        map.add("lastName","Gao");
        response = client1.path("customerservice/multiValuedMap").accept(MediaType.APPLICATION_FORM_URLENCODED).post(Entity.form(map));
//        response = client2.target(BASE_LOCATION + "customerservice/customers/bean").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        System.out.println(client1.getCurrentURI().getPath());
        System.out.println("Result of response.readEntity: " + response.readEntity(MultivaluedMap.class));
    }

    @Test
    public void cxf18() {
        System.out.println("Post customer using @BeanParam...");
        Form form = new Form();
        form.param("firstName","Jianbing");
        form.param("lastName","Gao");
        response = client1.path("customerservice/customers/bean").accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        response = client2.target(BASE_LOCATION + "customerservice/customers/bean").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        System.out.println(client1.getCurrentURI().getPath());
        System.out.println("Location is " + response.getLocation());
        System.out.println("Response Status is " + response.getStatus());
    }

    @Test
    public void cxf21() {
        String[] param = {"chevrolet","trax","cruze","2017"};
        File file = client1.path("customerservice/cars/{make}/{car1}/{car2}/year/{year}",param).accept(MediaType.TEXT_PLAIN).get(File.class);
        System.out.println("File name is:" + file.getName() + ", File size is:" + file.length());
    }

    @Test
    public void cxf22() {
        String[] param = new String[]{"chevrolet","trax","white","1410","2017"};
        String car = client1.path("customerservice/cars/{make}/{car1};color={color};weight={weight}/year/{year}",param).accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(car);
    }

    @Test
    public void cxf23() {
        String[] param = {"trax","cruze"};
        String car = client1.path("customerservice/cars/{car1}/{car2}",param).accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(car);
    }

    /**
     * GenericEntity doesn't work
     */
    @Test
    public void cxf24() {
        response = client1.path("customerservice/customers/generic").accept(MediaType.APPLICATION_JSON).get();
//        GenericEntity<List<Customer>> entity = response.getEntity();
        response.readEntity(String.class);
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void cxf25() {
        System.out.println("*** Created Customer,post Customer **");
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Smith");
        c.setId(123);
        c.setWeight("90");
        c.setBirthday(new Date());
        Address address = new Address();
        address.setCity("Suzhou");
        address.setState("Jiangsu");
        address.setStreet("Xinghu");
        address.setZip("215123");
        c.setAddress(address);
        response = client3.target(BASE_LOCATION + "customerservice/customers").request().post(Entity.json(c));
        System.out.println(response.readEntity(String.class));
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }

    @After
    public void destroy() {
        System.out.println(client1.getCurrentURI().getPath());
        client1.close();
        client2.close();
        if (response != null) {
            System.out.println("======================Response Headers======================");
            for (Iterator responseIterator = response.getHeaders().entrySet().iterator(); responseIterator.hasNext(); ) {
                Map.Entry<String,Object> next = (Map.Entry<String, Object>) responseIterator.next();
                System.out.println("Header: " + next.getKey() + ",Value:" + next.getValue());
            }
            response.close();
        }

    }
}
