package demo.jaxrs.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import demo.jaxrs.utils.exceptionMapper.MyExceptionMapper;
import demo.jaxrs.utils.provider.BearerTokenFilter;
import demo.jaxrs.utils.provider.JAXBProvider;
import demo.jaxrs.utils.provider.MaxAgeFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("v1")
public class AppConfig extends Application {
    private Set<Class<?>> resources = new HashSet<>();
    private Set<Object> singletons = new HashSet<>();

    public AppConfig() {
        System.out.println("AppConfig loaded...");
//        register(CustomerServiceJerseyImpl.class);
//        register(JacksonJaxbJsonProvider.class);
        resources.add(CustomerServiceJerseyImpl.class);
//        resources.add(CustomerServiceJersey.class);
        resources.add(JacksonJsonProvider.class);
//        resources.add(BearerTokenFilter.class);
        resources.add(JAXBProvider.class);

        // singletons
        singletons.add(new MyExceptionMapper());
//        singletons.add(new CustomerServiceJerseyImpl());
//        singletons.add(new JAXBProvider());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return resources;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
