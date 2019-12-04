package zhaoqi.test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * 模拟Spring 加载配置文件中得bean 到beanDefinitionMap 当中
 *
 *
 *
 */
public class LoadSpringXmlTest {

	public static void main(String[] args) {
		/**
		 * Spring 加载配置文件主要分为两步
		 *
		 * 第一步是加载这个配置文件  		ClassPathResource classPathResource  = new ClassPathResource("BeanConfig.xml");
		 *
		 * 第二步是将配置文件中定义得bean 注册到beanFactory的beanDefinitionMap当中
		 */

		ClassPathResource classPathResource  = new ClassPathResource("BeanConfig.xml");
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory(); // <2>

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory); // <3>
		reader.loadBeanDefinitions(classPathResource); // <4>

		BeanDefinition bean1 = factory.getBeanDefinition("bean1");

		System.out.println(bean1);


	}
}
