package zhaoqi.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 如果加了@component注解的情况下
 * 其实在被扫描出来以后被注册的时候的beanDefnition也是ScannerGenericeBeanDefnition 他是AnnotatedBeanDefinition的一个子类
 *
 *
 */
@Component
@Import(Bean3.class)
public class Bean2 {

	@Bean
	public Bean1 bean1() {
		return new Bean1();

	}
}
