package zhaoqi.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zhaoqi.test.bean.Bean1;

@Configuration
@ComponentScan("zhaoqi.test")
public class AppConfg {

	@Bean
	public Bean1 bean1() {
		return new Bean1();
	}



}
