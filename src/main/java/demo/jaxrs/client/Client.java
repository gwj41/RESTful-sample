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

package demo.jaxrs.client;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

public final class Client {
    private static final String BASE_LOCATION = "http://localhost:8080/RESTful-sample/service/info/";

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        // Sent HTTP GET request to query all customer info
        /*
         * URL url = new URL("http://localhost:9000/customers");
         * System.out.println("Invoking server through HTTP GET to query all
         * customer info"); InputStream in = url.openStream(); StreamSource
         * source = new StreamSource(in); printSource(source);
         */

        // Sent HTTP GET request to query customer info
/*        System.out.println("Sent HTTP GET request to query customer info");
        URL url = new URL("http://localhost:8080/RESTful-sample/service/info/customerservice/customers/123");
        InputStream in = url.openStream();
        System.out.println(getStringFromInputStream(in));*/

        // Sent HTTP GET request to query sub resource product info
        System.out.println("\n");
//        System.out.println("Sent HTTP GET request to query sub resource product info");
//        url = new URL("http://localhost:8080/RESTful-sample/service/info/customerservice/orders/223");
//        in = url.openStream();
//        System.out.println(getStringFromInputStream(in));

        System.out.println("Sent WebClient test...");
        WebClient client1 = WebClient.create(BASE_LOCATION);
        client1 = client1.path("customerservice/customers/{id};gender=male","123").accept(MediaType.APPLICATION_XML);
        System.out.println(client1.get(String.class));

        System.out.println("Return complex type List");
        client1 = WebClient.create(BASE_LOCATION);
        String customers = client1.path("customerservice/allCustomers/list/").accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);

        System.out.println("Return complex type Map");
        client1 = WebClient.create(BASE_LOCATION);
        customers = client1.path("customerservice/allCustomers/map/").accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customers);

        System.out.println("Return complex type Map in Response");
        client1 = WebClient.create(BASE_LOCATION);
        Response mapResponse = client1.path("customerservice/allCustomers/map/response/").accept(MediaType.APPLICATION_JSON).get();
        mapResponse.bufferEntity();
        String resultMap = mapResponse.readEntity(String.class);
        System.out.println(resultMap);

        System.out.println("Return File object...");
        client1 = WebClient.create(BASE_LOCATION);
        File file = client1.path("customerservice/file/{filePath}", "customerFile.txt").accept(MediaType.TEXT_PLAIN).get(File.class);
        System.out.println("File is: " + file.getPath());

        System.out.println("Invoking getCustomer With Multiple PathSegments");
        client1 = WebClient.create(BASE_LOCATION);
        System.out.println(client1.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/Age",new String[]{"123","Wenjun"}).accept(MediaType.APPLICATION_JSON).get(String.class));

        System.out.println("Invoking getCustomer With UriInfo");
        client1 = WebClient.create(BASE_LOCATION);
        System.out.println(client1.path("customerservice/customers/{id};gender=male;nickName=Robbie/{firstName};address=suzhou/UriInfo", new String[]{"123", "Wenjun"}).get(String.class));

        System.out.println("Sent WebClient test...get json and xml format");
        client1 = WebClient.create(BASE_LOCATION);
        System.out.println(client1.path("customerservice/customers/{id}", "123").accept(new String[]{MediaType.APPLICATION_JSON}).get(String.class));

        System.out.println("Sent WebClient test...get StreamingOutput result");
        client1 = WebClient.create(BASE_LOCATION);
        System.out.println(client1.path("customerservice/customers/streamingOutput/{id}", "123").accept(new String[]{MediaType.TEXT_PLAIN}).get(String.class));

        System.out.println("Sent json2 WebClient test...");
        client1 = WebClient.create(BASE_LOCATION);
        System.out.println(client1.path("customerservice/customers/json2/{id}", "123").accept(MediaType.APPLICATION_JSON).get(String.class));

        System.out.println("Add customer test...");
//        Thread.currentThread().getClass().getClassLoader().getResourceAsStream("/customer.txt");
        String inputFile = Thread.currentThread().getContextClassLoader().getResource("customer.txt").getFile();
        File input = new File(inputFile);
        client1 = WebClient.create(BASE_LOCATION);
        client1.type("application/json;charset=UTF-8");
        Response response = client1.path("customerservice/customers").accept(MediaType.APPLICATION_JSON).post(input);
        response.bufferEntity();
        System.out.println("Status: " + response.getStatus());
        System.out.println("Location: " + response.getLocation());
        System.out.println(getStringFromInputStream((InputStream) (response.getEntity())));
        System.out.println("Result of response.readEntity: " + response.readEntity(String.class));
        response.close();

        System.out.println("Add customer1 test...");
        String inputFile1 = Thread.currentThread().getContextClassLoader().getResource("customer1.txt").getFile();
        File input1 = new File(inputFile1);
        client1 = WebClient.create(BASE_LOCATION);
        client1.type("application/xml;charset=UTF-8");
        Response response1 = client1.path("customerservice/customers").accept(MediaType.APPLICATION_JSON).post(input1);
        System.out.println("Location is " + response1.getLocation());
        System.out.println("Response Status is " + response1.getStatus());

        System.out.println("Sent json2 WebClient test,get Wenjun Gu...");
        client1 = WebClient.create(BASE_LOCATION);
        String[] name = {"Wenjun","Gu"};
        System.out.println(client1.path("customerservice/customers/json2/{firstName}-{lastName}", name).accept(MediaType.APPLICATION_JSON).get(String.class));

        System.out.println("Test @QueryParam List...");
        client1 = WebClient.create(BASE_LOCATION);
//        System.out.println(client7.path("customerservice/queryParam").query("first", "Gu").replaceQueryParam("first",name).get(String.class));
        System.out.println(client1.path("customerservice/queryParam").replaceQueryParam("first", name).get(Response.class));
        client1.close();

        javax.ws.rs.client.Client client2 = ClientBuilder.newClient();
        System.out.println("*** GET Created Customer **");
        String customer = client2.target(BASE_LOCATION + "customerservice/customers/{id}").resolveTemplate("id",123).request().accept(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println(customer);

        client2 = ClientBuilder.newClient();
        System.out.println("*** GET Created Customer,post Form **");
        Form form = new Form();
        form.param("firstName","Jianbing");
        form.param("lastName","Gao");
        response1 = client2.target(BASE_LOCATION + "customerservice/customers/form").request().accept(MediaType.APPLICATION_JSON).post(Entity.form(form));
        System.out.println(response1.readEntity(String.class));
        client2.close();
        response1.close();
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }

}
