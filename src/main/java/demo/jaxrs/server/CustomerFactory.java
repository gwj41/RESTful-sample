package demo.jaxrs.server;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guxm on 2017/2/2.
 */
public enum CustomerFactory {
    instance;
    private Map<Long, Customer> customers = new ConcurrentHashMap<>();

    CustomerFactory() {
        Customer c = new Customer();
        c.setFirstName("John");
        c.setLastName("Smith");
        c.setId(123);
        c.setWeight("90");
        c.setBirthday(new Date());
        c.setJsonIgnore("Ignore Json property");
        c.setXmlIgnore("Ignore XML property");
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
        c.setBirthday(new Date());
        c.setJsonIgnore("Ignore Json property");
        c.setXmlIgnore("Ignore XML property");
        Address address1 = new Address();
        address1.setCity("Shanghai");
        address1.setState("Pudong");
        address1.setStreet("Nanjing Road");
        address1.setZip("123987");
        c1.setAddress(address1);
        customers.put(c1.getId(), c1);
    }

    public Map<Long,Customer> getCustomers() {
        return customers;
    }
}
