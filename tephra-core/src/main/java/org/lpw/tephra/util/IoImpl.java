package org.lpw.tephra.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author lpw
 */
@Component("tephra.util.io")
public class IoImpl implements Io {
    private static final int BUFFER_SIZE = 1024;

    @Autowired
    protected Logger logger;
    @Autowired
    protected Validator validator;

    @Override
    public byte[] read(String path) {
        if (validator.isEmpty(path))
            return null;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            InputStream input = new FileInputStream(path);
            copy(input, output);
            input.close();
            output.close();

            return output.toByteArray();
        } catch (IOException e) {
            logger.warn(e, "读文件[{}]时异常！", path);

            return null;
        }
    }

    @Override
    public void write(String path, byte[] content) {
        if (validator.isEmpty(path))
            return;

        try {
            if (logger.isDebugEnable())
                logger.debug("写入文件：{}", path);

            OutputStream output = new FileOutputStream(path);
            output.write(content);
            output.close();
        } catch (IOException e) {
            logger.warn(e, "写入文件[{}]时异常！", path);
        }
    }

    @Override
    public void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        for (int length; (length = input.read(buffer)) > -1; )
            output.write(buffer, 0, length);
        output.flush();
    }

    @Override
    public void write(OutputStream output, StringBuffer source) throws IOException {
        if (source == null || source.length() == 0)
            return;

        Writer writer = new OutputStreamWriter(output);
        for (int i = 0, length = source.length(); i < length; i++)
            writer.append(source.charAt(i));
        writer.flush();
    }
}
