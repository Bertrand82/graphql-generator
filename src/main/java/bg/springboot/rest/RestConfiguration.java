package bg.springboot.rest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.alps.AlpsJsonHttpMessageConverter;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;


@Configuration
public class RestConfiguration extends RepositoryRestConfigurerAdapter {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                entityManager.getMetamodel().getEntities().stream()
                .map(Type::getJavaType)
                .toArray(Class[]::new));
        config.setMaxPageSize(Integer.MAX_VALUE);
    }
    
    @Override
	public void configureJacksonObjectMapper(ObjectMapper objectMapper)	{
		super.configureJacksonObjectMapper(objectMapper);
	}
    
    @Override
	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    	System.err.println("       --------------------" +messageConverters);
    	for (HttpMessageConverter<?> m :messageConverters) {
    		System.err.println("-----" +m +"   -- ");

    		if (m instanceof AbstractJackson2HttpMessageConverter) {
    			AbstractJackson2HttpMessageConverter a = (AbstractJackson2HttpMessageConverter) m;
    			System.err.println("   xxxx ObjectMapper  "+a.getObjectMapper());
    			configureJacksonObjectMapper(a.getObjectMapper());
    		}
    	}
    	
    	super.configureHttpMessageConverters(messageConverters);
    	
    }
    
    
    
}