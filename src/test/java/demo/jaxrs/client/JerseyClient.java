package demo.jaxrs.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import demo.jaxrs.server.Address;
import demo.jaxrs.server.Customer;
import demo.jaxrs.utils.GZIPDecoder;
import demo.jaxrs.utils.GZIPEncoder;
import demo.jaxrs.utils.provider.JWTSecurityFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.net.ssl.SSLContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class JerseyClient {
    private static final String BASE_LOCATION = "http://localhost:8080/RESTful-sample/service";
    private Log logger = LogFactory.getLog(JerseyClient.class);
    private Client client2;
    private Client client3;
    private Response response;
    private WebTarget target;
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiIsInppcCI6IkRFRiJ9.eNocyrEKgDAMRdF_yVyhVbHq6Ojq4FxrhjiINA1YxH83OJ773gPEDCPMmBhLtWCURLlUU2CKYIBl03XF85BTGWRXCmNS4H3B6Nqh9p1tfGOAQv6DdV3_hyOT3h28HwAAAP__.AR7K6M-0w21cXsxkgut18STBma11yzIe0r8BnTS6P0qNGXz_EJm-4BG7ugrKFYTKQUFekqE_XV8rwxqcRH78WA";
    @Before
    public void init() {
        List<Class> providers = new ArrayList();
//        providers.add(GZIPDecoder.class);
//        providers.add(GZIPEncoder.class);
//        client2 = ClientBuilder.newBuilder().build();
//        client2 = ClientBuilder.newBuilder().register(GZIPInInterceptor.class).build();
        client2 = ClientBuilder.newBuilder().sslContext(buildSslContext(true)).register(JacksonJsonProvider.class).build();
        target = client2.target(BASE_LOCATION);
        client3 = ClientBuilder.newBuilder().property("connection.timeout",100).register(JacksonJaxbJsonProvider.class).build();
    }

    @Test
    public void cxf1() {
        System.out.println("Get sub resource");
        System.out.println(target.path("customerservice/{id}/delegate/firstName").resolveTemplate("id",123).request().accept(new String[]{MediaType.TEXT_PLAIN}).get(String.class));
    }

    @Test
    public void cxf2() throws Exception {
        System.out.println("Add customer test...");
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer.txt").getFile();
        File input = new File(inputFile);
        response = target.path("customerservice/customer/stream").request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(input,MediaType.APPLICATION_JSON));
        response.bufferEntity();
        System.out.println("Status: " + response.getStatus());
        System.out.println("Location: " + response.getLocation());
        System.out.println(getStringFromInputStream((InputStream) (response.getEntity())));
        System.out.println("Result of response.readEntity: " + response.readEntity(String.class));
    }

    @Test
    public void cxf3() {
        System.out.println("Sent WebClient test...get StreamingOutput result");
        System.out.println(target.path("customerservice/customers/streamingOutput/{id}").resolveTemplate("id",123).request().accept(new String[]{MediaType.TEXT_PLAIN}).get(String.class));
    }

    /**
     * Multiple PathSegments and CacheControl test
     */
    @Test
    public void cxf4() {
        System.out.println("Invoking getCustomer With Multiple PathSegments");
        WebTarget webTarget = target.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/Age");
        Map param = new HashMap();
        param.put("id","123");
        param.put("firstName","Wenjun");
//        webTarget.resolveTemplates(param);
        Invocation.Builder builder = webTarget.resolveTemplates(param).request().accept(MediaType.APPLICATION_JSON);
        response = builder.get();
        response.bufferEntity();
        System.out.println(response.readEntity(String.class));
        // Cached customer
//        WebClient client4 = client1.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/Age",new String[]{"123","Wenjun"}).accept(MediaType.APPLICATION_JSON);
        builder.header(HttpHeaders.ETAG,response.getHeaderString(HttpHeaders.ETAG)).header(HttpHeaders.IF_UNMODIFIED_SINCE,response.getHeaderString(HttpHeaders.LAST_MODIFIED));
        response = builder.get();
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void cxf5() {
        System.out.println("Invoking getCustomer With UriInfo");
        System.out.println(target.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/UriInfo").resolveTemplate("id",123).resolveTemplate("firstName","Wenjun").request().get(String.class));
    }

    @Test
    public void cxf6() {
        System.out.println("Return File object...");
        File file = target.path("customerservice/file/{filePath}").resolveTemplate("filePath","customerFile.txt").request().accept(MediaType.TEXT_PLAIN).get(File.class);
        System.out.println("File is: " + file.getPath());
    }

    @Test
    public void cxf7() {
        System.out.println("Sent WebClient test...get json and xml format");
        System.out.println(target.path("customerservice/customers/{id};gender=male").resolveTemplate("id",123).request().header(HttpHeaders.WWW_AUTHENTICATE,"abcde").accept(new String[]{MediaType.APPLICATION_XML}).get(String.class));
    }

    @Test
    public void cxf8() {
        System.out.println("Sent json2 WebClient test...");
        response = target.path("customerservice/customers/json2/{id}").resolveTemplate("id",123).request().accept(MediaType.APPLICATION_JSON).get();
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void cxf9() {
        System.out.println("Sent json2 WebClient test,get Wenjun Gu...");
        String[] name = {"Wenjun","Gu"};
        Map param = new HashMap();
        param.put("firstName","Wenjun");
        param.put("lastName","Gu");
        System.out.println(target.path("customerservice/customers/json2/{firstName}-{lastName}").resolveTemplates(param).request().accept(MediaType.APPLICATION_JSON).buildGet().invoke(String.class));
    }

    @Test
    public void cxf10() {
/*        Scanner scanner = new Scanner(System.in);
        String inputValue = scanner.next();
        String[] name = {inputValue,"Gu"};*/
        String[] name = {"Wenjun","Gu"};
        System.out.println("Test @QueryParam List...");
        response = target.path("customerservice/queryParam").queryParam("first",name).request().get();
        System.out.println(response.readEntity(List.class));
    }

    @Test
    public void cxf11() {
        System.out.println("Return complex type List");
        String customers = target.path("customerservice/allCustomers/list/").request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);
    }

    @Test
    public void cxf12() {
        System.out.println("Return complex type Map");
        String customers = target.path("customerservice/allCustomers/map/").request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);
    }

    @Test
    public void cxf13() {
        System.out.println("Return complex type Map in Response");
        response = target.path("customerservice/allCustomers/map/response/").request().accept(MediaType.APPLICATION_JSON).get();
        response.bufferEntity();
        String resultMap = response.readEntity(String.class);
        System.out.println(resultMap);
    }

    @Test
    public void cxf14() {
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer2.txt").getFile();
        File input = new File(inputFile);
//        client2.type("application/json;charset=UTF-8");
        response = target.path("customerservice/customers/{id}").resolveTemplate("id",123).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(input,MediaType.APPLICATION_JSON));
        response.bufferEntity();
        System.out.println("Updated customer: " + response.readEntity(String.class));
    }

    @Test
    public void cxf15_1() throws Exception {
        System.out.println("Add customer test...");
//        Thread.currentThread().getClass().getClassLoader().getResourceAsStream("/customer.txt");
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer.txt").getFile();
        File input = new File(inputFile);
//        client2.type("application/json;charset=UTF-8");
        response = target.path("customerservice/customers").request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(input,MediaType.APPLICATION_JSON));
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
//        client1.type("application/xml;charset=UTF-8");
        response = target.path("customerservice/customers").request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(input1,MediaType.APPLICATION_XML));
        System.out.println("Location is " + response.getLocation());
        System.out.println("Response Status is " + response.getStatus());
    }

    @Test
    public void cxfJAXRSClient1() {
        System.out.println("*** GET Created Customer **");
        String customer = target.path("customerservice/customers/{id}").resolveTemplate("id",123).request().accept(MediaType.APPLICATION_JSON).get(String.class);
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
        response = target.path("customerservice/customers/form").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void cxf17() {
        System.out.println("Post customer with MultivaluedMap...");
        MultivaluedMap<String,String> map = new MultivaluedStringMap();
        map.add("firstName","Jianbing");
        map.add("lastName","Gao");
        response = target.path("customerservice/multiValuedMap").request().accept(MediaType.APPLICATION_FORM_URLENCODED).post(Entity.form(map));
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
        response = target.path("customerservice/customers/bean").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        response = client2.target(BASE_LOCATION + "customerservice/customers/bean").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        System.out.println(client1.getCurrentURI().getPath());
        System.out.println("Location is " + response.getLocation());
        System.out.println("Response Status is " + response.getStatus());
    }

    @Test
    public void cxf21() {
//        String[] param = {"chevrolet","trax","cruze","2017"};
        Map param = new HashMap();
        param.put("make","chevrolet");
        param.put("car1","trax");
        param.put("car2","cruze");
        param.put("year","2017");
        File file = target.path("customerservice/cars/{make}/{car1}/{car2}/year/{year}").resolveTemplates(param).request().accept(MediaType.TEXT_PLAIN).get(File.class);
        System.out.println("File name is:" + file.getName() + ", File size is:" + file.length() + ",File path is:" + file.getAbsolutePath());
    }

    @Test
    public void cxf22() {
//        String[] param = new String[]{"chevrolet","trax","white","1410","2017"};
        Map param = new HashMap();
        param.put("make","chevrolet");
        param.put("car1","trax");
        param.put("color","white");
        param.put("weight","1410");
        param.put("year","2017");
        String car = target.path("customerservice/cars/{make}/{car1};color={color};weight={weight}/year/{year}").resolveTemplates(param).request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(car);
    }

    @Test
    public void cxf23() {
//        String[] param = {"trax","cruze"};
        Map param = new HashMap();
        param.put("car1","trax");
        param.put("car2","cruze");
        String car = target.path("customerservice/cars/{car1}/{car2}").resolveTemplates(param).request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(car);
    }

    /**
     * GenericEntity doesn't work
     */
    @Test
    public void cxf24() {
        response = target.path("customerservice/customers/generic").request().accept(MediaType.APPLICATION_JSON).get();
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
        c.setId(222);
        c.setWeight("90");
        c.setBirthday(new Date());
        Address address = new Address();
        address.setCity("Suzhou");
        address.setState("Jiangsu");
        address.setStreet("Xinghu");
        address.setZip("215123");
        c.setAddress(address);
        response = target.path("customerservice/customers").request().post(Entity.json(c));
        System.out.println(response.readEntity(String.class));
    }

    /**
     * Request a token form server
     */
    @Test
    public void jwt() {
        Form form = new Form();
        form.param("username","Wenjun");
        form.param("password","123456");
        response = target.path("customerservice/token").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
//        System.out.println(response.readEntity(String.class));
        logger.info(response.readEntity(String.class));
    }

    /**
     * Request with a token
     */
    @Test
    public void requestWithToken() {
        System.out.println("*** GET Created Customer **");
        String customer = target.path("customerservice/customers/{id}").resolveTemplate("id",123).request().header(HttpHeaders.AUTHORIZATION,TOKEN).accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customer);
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[in.available()];
        while (in.read(buffer) != -1) {
            bos.write(buffer);
        }
        bos.flush();
        in.close();
        bos.close();
        return bos.toString();
    }

    @After
    public void destroy() {
//        System.out.println(response.getLocation().getPath());
        client2.close();
        if (response != null) {
            System.out.println("======================Response Status is " + response.getStatusInfo().getStatusCode());
            System.out.println("======================Response Headers======================");
            for (Iterator responseIterator = response.getHeaders().entrySet().iterator(); responseIterator.hasNext(); ) {
                Map.Entry<String,Object> next = (Map.Entry<String, Object>) responseIterator.next();
                System.out.println("Header: " + next.getKey() + ",Value:" + next.getValue());
            }
//            response.close();
        }
    }

    private SSLContext buildSslContext(boolean admin) {
        String keystore;
        if (admin) {
            keystore = "E:\\development\\certificate\\client\\restAdminClient.p12";
        } else {
            keystore = "E:\\development\\certificate\\client\\restUserClient.p12";
        }
        final SslConfigurator sslConfigurator = SslConfigurator.newInstance().trustStoreFile(keystore).trustStorePassword("restful").keyStoreFile(keystore).keyStorePassword("restful").keyPassword("restful");
        final SSLContext sslContext = sslConfigurator.createSSLContext();
        return sslContext;
    }
}
