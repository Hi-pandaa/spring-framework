package zhaoqi.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("zhaoqi.beans")
public class AppConfig {




	@Bean
	public BeanDefinitionRegistryTest beanDefinitionRegistryTest(){
		return new BeanDefinitionRegistryTest();
	}

}
