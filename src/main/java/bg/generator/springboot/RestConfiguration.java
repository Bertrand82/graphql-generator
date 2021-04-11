package bg.generator.springboot;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;


@Configuration

public class RestConfiguration implements  RepositoryRestConfigurer {

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
	public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    	for (HttpMessageConverter<?> m :messageConverters) {
    		
    		if (m instanceof AbstractJackson2HttpMessageConverter) {
    			AbstractJackson2HttpMessageConverter a = (AbstractJackson2HttpMessageConverter) m;
    			configureJacksonObjectMapper(a.getObjectMapper());
    		}
    	} 
    	
    }
  
   
    
}