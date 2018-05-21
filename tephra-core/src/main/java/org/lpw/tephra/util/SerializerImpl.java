package org.lpw.tephra.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private KryoPool kryoPool = new KryoPool.Builder(Kryo::new).build();

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

        Kryo kryo = kryoPool.borrow();
        Output output = new Output(outputStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        kryoPool.release(kryo);
    }

    @Override
    public <T> T unserialize(byte[] bytes) {
        return validator.isEmpty(bytes) ? null : unserialize(new ByteArrayInputStream(bytes));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(InputStream inputStream) {
        Kryo kryo = kryoPool.borrow();
        Input input = new Input(inputStream);
        T object = (T) kryo.readObject(input, kryo.readClass(input).getType());
        input.close();
        kryoPool.release(kryo);

        return object;
    }
}
