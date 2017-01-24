package demo.jaxrs.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import demo.jaxrs.server.Address;
import demo.jaxrs.server.Customer;
import demo.jaxrs.utils.JSONTool;
import demo.jaxrs.utils.XMLTool;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.Date;

public class CreateData {

    @Test
    public void createJsonData() throws JsonProcessingException {
        System.out.println(JSONTool.toJson(createCustomer()));
    }

    @Test
    public void createXMLData() throws JAXBException {
        System.out.println(XMLTool.marshalToXML(createCustomer(),Customer.class));
    }

    private Customer createCustomer() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Smith");
        c.setId(1);
        c.setWeight("90");
        c.setBirthday(new Date());
        Address address = new Address();
        address.setCity("Suzhou");
        address.setState("Jiangsu");
        address.setStreet("Xinghu");
        address.setZip("215123");
        c.setAddress(address);
        return c;
    }
}
