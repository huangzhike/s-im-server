package mmp.im.common.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SpringContextHolder.applicationContext = applicationContext;

    }

    private static ApplicationContext getContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getContext().getBean(name, clazz);
    }


    public static <T> void setBean(T object) {
        getContext().getAutowireCapableBeanFactory().applyBeanPostProcessorsAfterInitialization(object, object.getClass().getName());
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) getContext().getAutowireCapableBeanFactory();

        beanFactory.registerSingleton(object.getClass().getName(), object);

    }


    /**
     * 直接创建bean，不设置属性
     *
     * @param beanId
     * @param clazz
     * @return
     */
    public static boolean registryBean(String beanId, Class<?> clazz) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        BeanDefinition definition = builder.getBeanDefinition();
        getRegistry().registerBeanDefinition(beanId, definition);
        return true;
    }


    /**
     * 为已知的class创建bean，可以设置bean的属性，可以用作动态代理对象的bean扩展
     *
     * @param beanId
     * @param
     * @return
     */
    public static boolean registryBeanWithEdit(String beanId, Class<?> factoryClazz, Class<?> beanClazz) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("myClass", beanClazz);
        definition.setBeanClass(factoryClazz);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        getRegistry().registerBeanDefinition(beanId, definition);
        return true;
    }

    /**
     * 为已知的class创建bean，可以设置bean的属性，可以用作动态代理对象的bean扩展
     *
     * @param beanId
     * @param
     * @return
     */
    public static boolean registryBeanWithDymicEdit(String beanId, Class<?> factoryClazz, Class<?> beanClazz, String params) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("interfaceClass", beanClazz);
        definition.getPropertyValues().add("params", params);
        definition.setBeanClass(factoryClazz);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        getRegistry().registerBeanDefinition(beanId, definition);
        return true;
    }

    /**
     * 获取注册者
     * context->beanfactory->registry
     *
     * @return
     */
    public static BeanDefinitionRegistry getRegistry() {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }


    ///////////////////////////////////////////////////////////////


}
