package org.lpw.tephra.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.upload.UploadReader;
import org.lpw.tephra.storage.Storage;
import org.lpw.tephra.util.Io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
public class HttpUploadReader implements UploadReader {
    private FileItem item;
    private String fieldName;
    private String fileName;
    private String contentType;
    private long size;
    private InputStream inputStream;
    private byte[] bytes;

    HttpUploadReader(FileItem item) throws IOException {
        this.item = item;
        fieldName = item.getFieldName();
        fileName = item.getName();
        contentType = item.getContentType();
        size = item.getSize();
        inputStream = item.getInputStream();
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, inputStream);
    }

    @Override
    public void delete() throws IOException {
        item.delete();
    }
}
