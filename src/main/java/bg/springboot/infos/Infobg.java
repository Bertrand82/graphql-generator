package bg.springboot.infos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * http://localhost:8080/welcome
 * @author w1
 *
 */
@RestController
public class Infobg {
	
	@Value("${spring.data.rest.base-path}")
	private String basePath ;
	
	@RequestMapping("/welcome")
	public String index() {
		return "Application de test de generation de graphQL  data.rest.base-path: "+basePath;
	}
	
	@RequestMapping("/version")
	public String version() {
		return "1.0.4-SNAPSHOT";
	}


}
