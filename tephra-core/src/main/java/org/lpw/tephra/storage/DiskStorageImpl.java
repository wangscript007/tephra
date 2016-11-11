package org.lpw.tephra.storage;

import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Io;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @auth lpw
 */
@Component("tephra.storage.disk")
public class DiskStorageImpl implements Storage {
    @Autowired
    protected Context context;
    @Autowired
    protected Io io;

    @Override
    public String getType() {
        return "disk";
    }

    @Override
    public void mkdirs(String path) {
        new File(path).mkdirs();
    }

    @Override
    public void read(String path, OutputStream outputStream) throws IOException {
        InputStream inputStream = getInputStream(path);
        io.copy(inputStream, outputStream);
        inputStream.close();
    }

    @Override
    public void write(String path, InputStream inputStream) throws IOException {
        OutputStream outputStream = getOutputStream(path);
        io.copy(inputStream, outputStream);
        outputStream.close();
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return new FileInputStream(getAbsolutePath(path, false));
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return new FileOutputStream(getAbsolutePath(path, true));
    }

    @Override
    public void delete(String path) {
        new File(getAbsolutePath(path, false)).delete();
    }

    protected String getAbsolutePath(String path, boolean parent) {
        String absolutePath = context.getAbsolutePath(path);
        if (parent)
            new File(absolutePath.substring(0, absolutePath.lastIndexOf('/'))).mkdirs();

        return absolutePath;
    }
}
