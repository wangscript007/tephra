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
        } catch (IOException e) {
            logger.warn(e, "序列化[{}]时发生异常！", object);
        }
    }

    @Override
    public <T> T unserialize(byte[] bytes) {
        if (validator.isEmpty(bytes))
            return null;

        return unserialize(new ByteArrayInputStream(bytes));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> T unserialize(InputStream inputStream) {
        if (inputStream == null)
            return null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            T object = (T) objectInputStream.readObject();
            objectInputStream.close();

            return object;
        } catch (Exception e) {
            logger.warn(e, "反序列化时发生异常！");

            return null;
        }
    }
}
