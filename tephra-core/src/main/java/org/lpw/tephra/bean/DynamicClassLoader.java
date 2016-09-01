package org.lpw.tephra.bean;

import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
public class DynamicClassLoader extends ClassLoader {
    protected Validator validator;
    protected Io io;
    protected Logger logger;
    protected ClassReloader reloader;

    public DynamicClassLoader(ClassLoader parent) {
        super(parent);

        validator = BeanFactory.getBean(Validator.class);
        io = BeanFactory.getBean(Io.class);
        logger = BeanFactory.getBean(Logger.class);
        reloader = BeanFactory.getBean(ClassReloader.class);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!reloader.isReloadEnable(name))
            return findLoadedClassByName(name);

        Class<?> clazz = findLoadedClass(name);
        if (clazz != null)
            return clazz;

        try {
            byte[] by = read(name);
            clazz = defineClass(name, by, 0, by.length);
            resolveClass(clazz);

            return clazz;
        } catch (Exception e) {
            logger.warn(e, "加载类[{}]时发生异常！", name);

            return null;
        }
    }

    protected Class<?> findLoadedClassByName(String name) throws ClassNotFoundException {
        Class<?> clazz = getParent().loadClass(name);
        if (clazz != null)
            return clazz;

        return findSystemClass(name);
    }

    protected byte[] read(String name) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = new FileInputStream(new StringBuilder().append(reloader.getClassPath()).append(File.separatorChar)
                .append(name.replace('.', File.separatorChar)).append(".class").toString());
        io.copy(input, output);
        input.close();
        output.close();

        return output.toByteArray();
    }
}
