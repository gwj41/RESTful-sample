package demo.jaxrs.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JSONTool {
    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonGenerator jsonGenerator = null;
    public static String toJson(Object object) {
        if (jsonGenerator == null) {
            try {
                jsonGenerator = mapper.getFactory().createGenerator(System.out, JsonEncoding.UTF8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapper.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
            jsonGenerator.writeObject(object);
            System.out.println(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
        try {
            if (jsonGenerator != null) {
                jsonGenerator.flush();
            }
            if (!jsonGenerator.isClosed()) {
                jsonGenerator.close();
            }
            jsonGenerator = null;
//            mapper = null;
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> T toObject(String json,Class<T> type) {
        T obj = null;
        try {
            obj = mapper.readValue(json,type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
