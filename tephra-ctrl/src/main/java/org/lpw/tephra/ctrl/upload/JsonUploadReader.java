package org.lpw.tephra.ctrl.upload;

import com.alibaba.fastjson.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.storage.Storage;
import org.lpw.tephra.util.Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lpw
 */
public class JsonUploadReader implements UploadReader {
    private String name;
    private String fileName;
    private String contentType;
    private String base64;
    private byte[] bytes;

    JsonUploadReader(JSONObject object) {
        this(object.getString("fieldName"), object.getString("fileName"), object.getString("contentType"),
                object.getString("base64"));
    }

    JsonUploadReader(String name, String fileName, String contentType, String base64) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.base64 = base64;
    }

    @Override
    public String getName() {
        return name;
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
        return getBytes().length;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, getBytes());
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    @Override
    public byte[] getBytes() {
        if (bytes == null)
            bytes = BeanFactory.getBean(Coder.class).decodeBase64(base64);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
    }
}
