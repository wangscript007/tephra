package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author lpw
 */
@Component("tephra.util.io")
public class IoImpl implements Io {
    private static final int BUFFER_SIZE = 1024;

    @Inject
    private Logger logger;
    @Inject
    private Validator validator;

    @Override
    public void mkdirs(File file) {
        if (file == null || file.exists())
            return;

        if (!file.mkdirs())
            logger.warn(null, "创建目录（集）[{}]失败！", file.getAbsolutePath());
        else if (logger.isDebugEnable())
            logger.debug("成功创建目录（集）[{}]。", file.getAbsolutePath());
    }

    @Override
    public void mkdirs(String path) {
        mkdirs(new File(path));
    }

    @Override
    public byte[] read(String path) {
        ByteArrayOutputStream outputStream = readAsStream(path);

        return outputStream == null ? null : outputStream.toByteArray();
    }

    @Override
    public String readAsString(String path) {
        ByteArrayOutputStream outputStream = readAsStream(path);

        return outputStream == null ? null : outputStream.toString();
    }

    @Override
    public byte[] read(InputStream inputStream) {
        ByteArrayOutputStream outputStream = readAsStream(inputStream);

        return outputStream == null ? null : outputStream.toByteArray();
    }

    @Override
    public String readAsString(InputStream inputStream) {
        ByteArrayOutputStream outputStream = readAsStream(inputStream);

        return outputStream == null ? null : outputStream.toString();
    }

    private ByteArrayOutputStream readAsStream(String path) {
        if (validator.isEmpty(path) || !new File(path).exists())
            return null;


        try {
            return readAsStream(new FileInputStream(path));
        } catch (IOException e) {
            logger.warn(e, "读文件[{}]时异常！", path);

            return null;
        }
    }

    private ByteArrayOutputStream readAsStream(InputStream inputStream) {
        if (inputStream == null)
            return null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();

            return outputStream;
        } catch (IOException e) {
            logger.warn(e, "读取输入流时异常！");

            return null;
        }
    }

    @Override
    public void write(String path, byte[] bytes) {
        if (validator.isEmpty(path))
            return;

        if (logger.isDebugEnable())
            logger.debug("写入文件：{}", path);

        try {
            OutputStream outputStream = new FileOutputStream(path);
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "写入文件[{}]时异常！", path);
        }
    }

    @Override
    public void write(String path, InputStream inputStream) {
        if (validator.isEmpty(path))
            return;

        if (logger.isDebugEnable())
            logger.debug("写入文件：{}", path);

        try {
            OutputStream outputStream = new FileOutputStream(path);
            copy(inputStream, outputStream);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "写入文件[{}]时异常！", path);
        }
    }

    @Override
    public void copy(String input, String output) {
        try {
            OutputStream outputStream = new FileOutputStream(output);
            InputStream inputStream = new FileInputStream(input);
            copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "复制文件[{}->{}]时发生异常！", input, output);
        }
    }

    @Override
    public void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        for (int length; (length = inputStream.read(buffer)) > -1; )
            outputStream.write(buffer, 0, length);
        outputStream.flush();
    }

    @Override
    public void write(OutputStream outputStream, StringBuffer source) throws IOException {
        if (source == null || source.length() == 0)
            return;

        Writer writer = new OutputStreamWriter(outputStream);
        for (int i = 0, length = source.length(); i < length; i++)
            writer.append(source.charAt(i));
        writer.flush();
    }

    @Override
    public void close(InputStream inputStream) {
        if (inputStream == null)
            return;

        try {
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "关闭输入流时发生异常！");
        }
    }

    @Override
    public void close(OutputStream outputStream) {
        if (outputStream == null)
            return;

        try {
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "关闭输出流时发生异常！");
        }
    }

    @Override
    public File move(File source, File target) {
        mkdirs(target.getParentFile());

        return source.renameTo(target) ? target : source;
    }

    @Override
    public String move(String source, String target) {
        return move(new File(source), new File(target)).getAbsolutePath();
    }

    @Override
    public void delete(File file) {
        if (file == null)
            return;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files)
                    delete(f);
        }
        if (!file.delete())
            logger.warn(null, "删除文件/目录[{}]失败！", file.getAbsolutePath());
        else if (logger.isDebugEnable())
            logger.debug("成功删除文件/目录[{}]。", file.getAbsolutePath());
    }

    @Override
    public void delete(String path) {
        delete(new File(path));
    }
}
