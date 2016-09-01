package org.lpw.tephra.bean;

import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.bean.class-reloader")
public class ClassReloaderImpl implements ClassReloader, MinuteJob, ApplicationContextAware {
    @Autowired
    protected Converter converter;
    @Autowired
    protected Context context;
    @Autowired
    protected Io io;
    @Autowired
    protected Validator validator;
    @Autowired
    protected Generator generator;
    @Autowired
    protected Logger logger;
    @Autowired
    protected Container container;
    @Value("${tephra.bean.reload.class-path:}")
    protected String classPath;
    protected List<ClassLoader> loaders;
    protected Set<String> names;
    protected Map<Class<?>, List<Autowire>> autowires;
    protected ApplicationContext applicationContext;

    @Override
    public boolean isReloadEnable(String name) {
        return names.contains(name);
    }

    @Override
    public String getClassPath() {
        return context.getAbsolutePath(classPath);
    }

    @Override
    public void executeMinuteJob() {
        if (validator.isEmpty(classPath))
            return;

        names = names();
        if (names.isEmpty())
            return;

        if (logger.isInfoEnable())
            logger.info("重新载入类：{}", names);

        if (loaders == null) {
            loaders = new ArrayList<>();
            loaders.add(applicationContext.getClassLoader());
        }

        if (autowires == null) {
            autowires = new ConcurrentHashMap<>();

            for (String name : container.getBeanNames())
                autowire(container.getBeanClass(name), name, null);
        }

        ClassLoader loader = new DynamicClassLoader(loaders.get(loaders.size() - 1));
        names.forEach((name) -> load(loader, name));
        loaders.add(loader);
    }

    protected Set<String> names() {
        Set<String> set = new HashSet<>();
        String path = context.getAbsolutePath(classPath + "/name");
        String names = new String(io.read(path)).trim();
        if (validator.isEmpty(names))
            return set;

        for (String name : converter.toArray(names, "\n"))
            if (name.trim().length() > 0)
                set.add(name.trim());

        io.write(path, new byte[0]);

        return set;
    }

    protected void autowire(Class<?> beanClass, String beanName, Object bean) {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.getAnnotation(Autowired.class) == null)
                continue;

            try {
                field.setAccessible(true);
                Class<?> key = field.getType();
                boolean collection = isCollection(key);
                if (collection) {
                    Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (type instanceof Class)
                        key = (Class<?>) type;
                    else
                        continue;
                }

                List<Autowire> list = autowires.get(key);
                if (list == null)
                    list = new ArrayList<>();
                list.add(new Autowire(bean == null ? container.getBean(beanName) : bean, field, collection));
                autowires.put(key, list);
            } catch (Exception e) {
                logger.warn(e, "解析[{}]属性[{}]依赖时发生异常！", beanClass, field.getName());
            }
        }
    }

    protected boolean isCollection(Class<?> clazz) {
        try {
            return clazz.equals(clazz.asSubclass(Collection.class));
        } catch (Exception e) {
            return false;
        }
    }

    protected void load(ClassLoader loader, String name) {
        try {
            DefaultListableBeanFactory lbf = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(null, name, loader);
            String dynamicBeanName = generator.uuid();
            lbf.registerBeanDefinition(dynamicBeanName, bd);
            Object bean = lbf.getBean(dynamicBeanName);
            String beanName = getBeanName(bean.getClass());
            Object oldBean = null;
            if (beanName != null) {
                oldBean = container.getBean(beanName);
                container.mapBeanName(beanName, dynamicBeanName);
            }
            autowire(bean.getClass(), null, bean);
            autowired(bean, oldBean);
        } catch (Exception e) {
            logger.warn(e, "重新载入[{}]时发生异常！", name);
        }
    }

    protected String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null)
            return component.value();

        Repository repository = clazz.getAnnotation(Repository.class);
        if (repository != null)
            return repository.value();

        Service service = clazz.getAnnotation(Service.class);
        if (service != null)
            return service.value();

        Controller controller = clazz.getAnnotation(Controller.class);
        if (controller != null)
            return controller.value();

        return null;
    }

    @SuppressWarnings("unchecked")
    protected void autowired(Object bean, Object oldBean) throws IllegalArgumentException, IllegalAccessException {
        for (Class<?> key : autowires.keySet()) {
            if (!key.isInstance(bean))
                continue;

            for (Autowire autowire : autowires.get(key)) {
                Object value = bean;
                if (autowire.isCollection()) {
                    Collection<Object> collection = (Collection<Object>) autowire.getField().get(autowire.getBean());
                    if (oldBean != null)
                        collection.remove(oldBean);
                    collection.add(bean);
                    value = collection;
                }
                autowire.getField().set(autowire.getBean(), value);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
