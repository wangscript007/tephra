package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Component("tephra.util.serializer")
public class SerializerImpl implements Serializer {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;

    @Override
    public byte[] serialize(Object object) {
        if (object == null)
            return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serialize(object, outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public void serialize(Object object, OutputStream outputStream) {
        if (object == null || outputStream == null)
            return;

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "序列化对象[{}]时发生异常！", object);
        }
    }

    @Override
    public <T> T unserialize(byte[] bytes) {
        return validator.isEmpty(bytes) ? null : unserialize(new ByteArrayInputStream(bytes));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(InputStream inputStream) {
        if (inputStream == null)
            return null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            T object = (T) objectInputStream.readObject();
            objectInputStream.close();

            return object;
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(e, "反序列化对象时发生异常！");

            return null;
        }
    }
}
