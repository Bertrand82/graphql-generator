package bg.springboot.infos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("info")
public class Info {
	
	
	
	@RequestMapping("/")
	public String index() {
		return "Bg Greetings from Spring Boot!";
	}


}
