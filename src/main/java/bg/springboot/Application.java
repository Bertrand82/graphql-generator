package bg.springboot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;





@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = { "bg.spring.generated.pojo", "bg.spring.generated.controller",
		"bg.spring.generated.repository" })
@EnableJpaAuditing
public class Application implements WebSocketMessageBrokerConfigurer {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
				.properties(getProperties())
				.build().run(args);
	}

	private static String getProperties() {
		return "spring.config.location:" + "classpath:config-app.yml," + "classpath:config-auth.yml,"
				+ "classpath:config-database.yml," + "classpath:config-metrics.yml";
	}

}