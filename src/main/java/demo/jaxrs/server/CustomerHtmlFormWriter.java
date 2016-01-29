package demo.jaxrs.server;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wgu on 9/11/2015.
 */
@Produces(MediaType.TEXT_HTML)
@Provider
public class CustomerHtmlFormWriter implements MessageBodyWriter<Customer> {
    private static final String HTML_TEXT1 = "<html><head>Test Message Body Writer</head><body><div>Message Body Writer:</div>";
    private static final String HTML_TEXT_END = "</body></html>";
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//        List.class.isAssignableFrom(type);
        return Customer.class.isAssignableFrom(type);
    }

    public long getSize(Customer customer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        System.out.println("Writer.getSize...");
        return -1;
    }

    public void writeTo(Customer customer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        System.out.println("Writer to...");
        entityStream.write(HTML_TEXT1.getBytes());
        if (customer != null) {
            entityStream.write("<div>".getBytes());
            entityStream.write("<h2>first name is:".getBytes());
            entityStream.write((customer.getFirstName() + "</h2>").getBytes());
            entityStream.write("<br/>".getBytes());
            entityStream.write("last name is:".getBytes());
            entityStream.write(customer.getLastName().getBytes());
            entityStream.write("</div>".getBytes());

        }
        entityStream.write(HTML_TEXT_END.getBytes());
    }
}
