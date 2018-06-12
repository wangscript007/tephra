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
    private String string;
    private byte[] bytes;
    private InputStream inputStream;

    JsonUploadReader(JSONObject object) {
        this(object.getString("name"), object.getString("fileName"), object.getString("contentType"),
                object.getString("base64"), object.getString("string"));
    }

    JsonUploadReader(String name, String fileName, String contentType, String base64, String string) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.base64 = base64;
        this.string = string;
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
        if (inputStream == null)
            inputStream = new ByteArrayInputStream(getBytes());

        return inputStream;
    }

    @Override
    public byte[] getBytes() {
        if (bytes == null)
            bytes = base64 == null ? string.getBytes() : BeanFactory.getBean(Coder.class).decodeBase64(base64);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
    }
}
