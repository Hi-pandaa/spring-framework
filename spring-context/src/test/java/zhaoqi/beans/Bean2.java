package zhaoqi.beans;

import org.springframework.stereotype.Component;

/**
 * 如果加了@component注解的情况下
 * 其实在被扫描出来以后被注册的时候的beanDefnition也是ScannerGenericeBeanDefnition 他是AnnotatedBeanDefinition的一个子类
 *
 *
 */
@Component
public class Bean2 {
}
