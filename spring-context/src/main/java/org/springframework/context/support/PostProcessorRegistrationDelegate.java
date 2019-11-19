/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	/**
	 * 这个方法主要执行了各种beanFactoryPostProsessor 完成bean工厂的初始化
	 * 以及bean工厂的对所有的bean的扫描
	 *
	 *
	 * 使用的是策略模式 根据不同的实现接口(不同的策略予以区分 ) 虽然他们都是beanFactortPostProcessor/beanDefinitionRegistryPostProcessor的实现类
	 * 但是因为他们实现了不同的接口 也就是不同的策略 所以才执行的时候也会有所区分
	 *
	 * 然后分批执行
	 *
	 * @param beanFactory
	 * @param beanFactoryPostProcessors 开发者手动注册进去的beanFactoryPostProcessors 一般为空 初始化方式为 ioc.addBeanFactoryPostProcessor 而不是通过注解的方式
	 *                                  								  如果存在 spring会先对其进行处理 区分它是 beanFactoryProcessor还是一个 beanDefinitionRegistryPostProcessor
	 *
	 **
	 *
	 *
	 *
	 *          ***************spring 对bean工厂的后置处理器的执行顺序
	 *                                  1.优先执行由开发者使用API提供的beanFactoryPostProcessor    ---applicationContext.addBeanFactoryPostProcessor();
	 *                                  如果用这种方式加进去的benaFactoryPostProsessors会在spring 容器的configurationClassPostProcessor扫描之前就执行  跟加注解的不一样
	 *                                  这种情况用的比较少
	 *

	 *
	 *========================================================该方法的执行流程========================================================================
	 *                                 BeanDefinitionRegistryPostProcessor 执行流程(api/spring自己提供的 put到beanDefinitionMap当中/@bean|@import设置)
	 *									执行子类接口的方法
	 *
	 *
	 *                                  	1>执行通过API添加的beanDefinitionRegistryPostProcessor
	 *
	 *
	 *                                   2>spring 自己提供的后置处理器 +包括了程序员提供的实现了某些(PriorityOrdered/Ordered)策略的后置处理器
	 *                                  1>执行A 策略 加到已执行的集合当中
	 *                                  2>执行B 策略 且没有被执行过 (防止有交集)  加到已执行的集合当中
	 *                                  3>执行所有的 且没有被执行过的
	 *
	 *
	 *                                 3> 执行普通的beanDefnitionRegistryPostProcessor 不带任何策略的
	 *======================================================================================================================================
	 *
	 *                                  beanFactoryPostProsessor 执行流程(api/spring自己提供的 put到beanDefinitionMap当中/@bean|@import设置)
	 *
	 *                                  执行父类接口的方法
	 *
	 *                                  1>优先执行 BeanDefinitionRegistryPostProcessor子类 的父类接口 beanFactoryPostProsessor的方法
	 *                                  2>再执行只实现了父类的后置处理器的方法
	 *
	 *
	 *
	 *
	 */
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		//所有存在的BeanDefinitionRegistryPostProcessors的名字
		Set<String> processedBeans = new HashSet<>();


		//90%
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;


			//这里存放的是标准的beanFactoryPostProcessors

			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();

			//这里存放的是所有注册的  BeanDefinitionRegistryPostProcessor  但是 BeanDefinitionRegistryPostProcessor是这里存放的是标准的beanFactoryPostProcessor的子类接口
			//是通过API添加进去的beanFactoryPostProcessors


			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			//优先使用的是程序员使用api提供的beanFactoryPostProcessors
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					//如果是子接口 BeanDefinitionRegistryPostProcessor的实现类
					//就执行 postProcessBeanDefinitionRegistry方法
					// 1>=================第一次执行 执行的是开发者用API注入的beanDefinitionRegistryPostProcessor的子接口方法  但是没用执行父接口的beanFactortPostProcessord的方法
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					//添加到 registryProcessors
					registryProcessors.add(registryProcessor);
				}
				else {
					//添加到 regularPostProcessors
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			//使用策略模式 记录正在执行的 BeanDefinitionRegistryPostProcessors
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.


			//获取到所有的Spring 内置的 也就是再外层方法已经初始化好的 BeanDefinitionRegistryPostProcessor 的后置处理器
			//org.springframework.context.annotation.internalConfigurationAnnotationProcessor====ConfigurationClassPostProcessor
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				//符合PriorityOrdered策略
				// extra :  ConfigurationClassPostProcessor就是这个策略中的一个 执行以后会扫描所有的bd 如果扫描到的是带@configuration的类 就会在在AttributeAccessor
				//configurationClass = full   他的beanDefinition 是一个annotedBeanDefinition  然后这个类就会被spriing代理
				//如果扫到的是@component  就会变成一个@ScanneredBeanDefinition

				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					//如果符合 PriorityOrdered 策略
					//这里的getBean其实就是从beanDefinitionMap中的把bd给实例化了
					//添加到 currentRegistryProcessors 当前正要执行的 registryProcessor
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//已执行的集合添加
					processedBeans.add(ppName);
				}
			}
			//因为这个策略是带排序 送一先需要排序的
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//添加到 registryProcessors
			registryProcessors.addAll(currentRegistryProcessors);
			// 2>=================第二次执行 执行的是spring自带的的beanDefinitionRegistryPostProcessor的子接口方法（PriorityOrdered策略）  但是没用执行父接口的beanFactortPostProcessord的方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);

			//执行完成一种策略以后 就把执行完成的策略清除
			currentRegistryProcessors.clear();




			//因为上一个 PriorityOrdered策略中的ConfigurationClassPostProcessor 已经把我们在各种configuration 和@component中定义的beanFactoryPostProcessor全部扫出来了

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			//找orderly策略的所有beanFactoryPostProcessor
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				//没有被执行过且 符合Ordered策略
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//加到已经处理的beanNames 的集合当中去
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			// 3>=================第三次执行 执行的是spring自带的+开发者实现了ordered策略的注册后置处理器的beanDefinitionRegistryPostProcessor的子接口方法  但是没用执行父接口的beanFactortPostProcessord的方法

			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;

				//拿到所有的BeanDefinitionRegistryPostProcessor的实现类  这里不再需要判断是否有策略模式

				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					//这里判断是否被上面其他的策略执行过 如果执行过就不执行了

					if (!processedBeans.contains(ppName)) {
						//添加到正要执行的 currentRegistryProcessors 当中
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						//已执行的processedBeans添加
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				//排序
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				//添加到 registryProcessors
				registryProcessors.addAll(currentRegistryProcessors);
				//执行BeanDefinitionRegistryPostProcessor的方法
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				//清除当前正要执行的beanFactory的后置处理器
				currentRegistryProcessors.clear();
			}

			//===============================到这里为止  程序员使用API提供的+ 带策略的(spring内置的+用户配置的)+没用任何策略的beanDefinitionRegistry后置处理器 全部执行完成===============

			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			//执行BeanDefinitionRegistryPostProcessor的实现类 当中 最顶层父接口 postProcessBeanFactory的方法

			//其实在spring执行所有的beanFactory后置处理器的过程中  优先执行  beanDefinitionRegistryPostProcessor的方法 在执行父接口beanFactoryPostProcessor的方法

			//执行了实现了beanDefinitionRegistryPostProcessor的父接口的方法 postProcessBeanFactory的方法(子类接口已经被执行了)
			//extra : ConfigurationClassPostProcessor.postProcessBeanFactory();
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);

			/**
			 * 执行普通的beanFactoryPostProcessord的方法 (只有API注入的)
			 */
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		//获取所有的beanFactoryPostProcessor
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		//也要根据不同的策略去执行
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		//执行
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
