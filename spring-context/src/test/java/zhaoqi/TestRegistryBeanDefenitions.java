package zhaoqi;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zhaoqi.beans.AppConfig;
import zhaoqi.beans.BeanDefinitionRegistryTest2;

import java.util.concurrent.Executors;

public class TestRegistryBeanDefenitions {

	public static void main(String[] args) {


		AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(AppConfig.class);

	//	ioc.addBeanFactoryPostProcessor(new BeanDefinitionRegisztryTest2());

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
		//full

		System.out.println("=============================================");


	}
}
