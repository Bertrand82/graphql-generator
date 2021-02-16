package bg.springboot;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;





@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "bg.spring.generated"})
@ComponentScan(basePackages = { "bg.spring.generated", "bg.springboot","bg.generated.graphql"})
@EnableJpaAuditing
public class Application implements WebSocketMessageBrokerConfigurer {

	
	
	
	
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplicationBuilder(Application.class)
				.properties(getProperties())
				.build();
		ConfigurableApplicationContext context = springApplication.run(args);
		
		String[] beanNames = context.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			System.out.println("bg beanName ------------------------------"+beanName);
		}
		
	}

	private static String getProperties() {
		return "spring.config.location:" + "classpath:config-app.yml," + "classpath:config-auth.yml,"
				+ "classpath:config-database.yml," + "classpath:config-metrics.yml";
	}

}