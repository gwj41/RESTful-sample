package demo.jaxrs.utils;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class JAXBProvider implements MessageBodyWriter,MessageBodyReader {
    @Context
    protected Providers providers;
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class);
    }

    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            JAXBContext context = null;
            ContextResolver<JAXBContext> resolver = providers.getContextResolver(JAXBContext.class,mediaType);
            if (resolver != null) {
                context = resolver.getContext(type);
            }
            if (context == null) {
                context = JAXBContext.newInstance(type);
            }
            Marshaller marshaller = context.createMarshaller();
            boolean pretty = false;
            for (Annotation ann : annotations) {
                if (ann.annotationType().equals(Pretty.class)) {
                    pretty = true;
                    break;
                }
            }
            if (pretty) {
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            }
            marshaller.marshal(o,entityStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class);
    }

    public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            JAXBContext ctx = JAXBContext.newInstance(type);
            return ctx.createUnmarshaller().unmarshal(entityStream);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }
}
