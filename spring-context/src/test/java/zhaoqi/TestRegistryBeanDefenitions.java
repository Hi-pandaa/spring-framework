package zhaoqi;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zhaoqi.beans.AppConfig;

public class TestRegistryBeanDefenitions {

	public static void main(String[] args) {


		ApplicationContext ioc = new AnnotationConfigApplicationContext(AppConfig.class);

	}
}
