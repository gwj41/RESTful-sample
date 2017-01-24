package demo.jaxrs.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLTool {
    private static JAXBContext context;
    public static String marshalToXML(Object obj,Class type) throws JAXBException {
        context = JAXBContext.newInstance(type);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(obj,writer);
        return writer.toString();
    }

    public static <T> T unmarshalToObject(String xml,Class<T> type) throws JAXBException {
        context = JAXBContext.newInstance(type);
        return (T)context.createUnmarshaller().unmarshal(new StringReader(xml));
    }
}
