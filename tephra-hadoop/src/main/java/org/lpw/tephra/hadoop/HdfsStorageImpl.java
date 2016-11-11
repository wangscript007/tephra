package org.lpw.tephra.hadoop;

import org.lpw.tephra.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @auth lpw
 */
@Component("tephra.hadoop.hdfs.storage")
public class HdfsStorageImpl implements Storage {
    @Autowired
    protected Hdfs hdfs;

    @Override
    public String getType() {
        return "hdfs";
    }

    @Override
    public void mkdirs(String path) {
        hdfs.mkdirs(path);
    }

    @Override
    public void read(String path, OutputStream outputStream) throws IOException {
        hdfs.read(path, outputStream);
    }

    @Override
    public void write(String path, InputStream inputStream) throws IOException {
        hdfs.write(inputStream, path);
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return hdfs.getInputStream(path);
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return hdfs.getOutputStream(path);
    }

    @Override
    public void delete(String path) {
        hdfs.delete(path);
    }
}
