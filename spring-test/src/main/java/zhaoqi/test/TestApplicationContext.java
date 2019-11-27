package zhaoqi.test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zhaoqi.test.bean.Bean1;
import zhaoqi.test.config.AppConfg;

public class TestApplicationContext {

	public static void main(String[] args) {


		AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(AppConfg.class);

		System.out.println(ioc.getBean(Bean1.class));

		//区别不同的beanDefinition





		System.out.println("===========================================");

		// AnnotationConfigApplicationContext 构造 注入的带@configuration的bean
		// beanDefinitionClass   =AnnotatedGenericBeanDefinition
		// AttributeAccessor[configurationClass]=full

		BeanDefinition baseConfiguration = ioc.getBeanDefinition("appConfig");
		System.out.println(baseConfiguration.getClass());//容器入参的全配置的类
		System.out.println(baseConfiguration.getAttribute("org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass"));

		System.out.println("===========================================");

		// 带@configuration的bean 但是是被scan扫到的bean
		// beanDefinitionClass   =ScannedGenericBeanDefinition
		// AttributeAccessor[configurationClass]=full
		//extra  说明其实全配置类 attribute =full  只跟@configuration注解有关系
		//只要是@configuration 他的attribute =full 只要是全配置类 他就会被spring代理 代码触发位置 configuratiionClasspostProcessor .postProcessBeanFactory()
		//bd 类型跟他配置的方式有关

		BeanDefinition scannerConfiguration = ioc.getBeanDefinition("fullConfig");
		System.out.println(scannerConfiguration.getClass());//容器入参的全配置的类
		System.out.println(scannerConfiguration.getAttribute("org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass"));

		System.out.println("=============================================");

		//@bean 模式注入的bean 无论它是否处在全配置类下面
		//beanDefinitionClass = ConfigurationClassBeanDefinition
		// AttributeAccessor[configurationClass]=null

		BeanDefinition peizhiBean = ioc.getBeanDefinition("bean1");
		System.out.println(peizhiBean.getClass());//容器入参的全配置的类
		System.out.println(peizhiBean.getAttribute("org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass"));

		System.out.println("=============================================");

		//@component @service 注入的bean
		//beanDefinitionClass = ScannedGenericBeanDefinition
		// AttributeAccessor[configurationClass]=lite
		BeanDefinition scannerBean = ioc.getBeanDefinition("bean2");
		System.out.println(scannerBean.getClass());//容器入参的全配置的类
		System.out.println(scannerBean.getAttribute("org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass"));

		System.out.println("=============================================");
		//@import  注入的bean
		//beanDefinitionClass = AnnotatedGenericBeanDefinition
		// AttributeAccessor[configurationClass]=null
		BeanDefinition importBean = ioc.getBeanDefinition("zhaoqi.beans.Bean3");
		System.out.println(importBean.getClass());//容器入参的全配置的类
		System.out.println(importBean.getAttribute("org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass"));

		System.out.println("=============================================");


		//总结下
		//全配置类  AttributeAccessor[configurationClass] 属性
		//如果属性值是full 代表是一个全配置类
		//full 加了@configuration的类 一定是一个全配置类 会被Spring代理
		//lite 如果不是@bean的方式 或者 factoryBean的初始化方式  就会是lite
		//null 其他的情况


		//bd 的类型

		//========== AnnotatedGenericBeanDefinition ==========
		//1>AnnotationConfigApplicationContext 构造 注入的类 无论是否带@configuration
		//2>@import  注入的bean

		//==========ScannedGenericBeanDefinition===========
		//1>@ComponentScan扫到的带@component /@service bean

		//===========ConfigurationClassBeanDefinition==========
		//1>使用@Bean 初始化的bean 这种bean 在一开始的 configurationClassBeanDefinition 不会扫描到这个beanDefinition
		// 一直到它所在的bean被创建的时候才会生成一个对应的beanDefinition



		System.out.println("=============================================");

	}
}
