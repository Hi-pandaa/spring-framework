package zhaoqi.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zhaoqi.test.config.AppConfg;

public class TestApplicationContext {

	public static void main(String[] args) {


		ApplicationContext ioc = new AnnotationConfigApplicationContext(AppConfg.class);

		System.out.println(ioc.getBean("bean1"));

	}
}
